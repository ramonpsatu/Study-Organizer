package com.ramonpsatu.studyorganizer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ramonpsatu.studyorganizer.core.data.database.entity.Subject
import com.ramonpsatu.studyorganizer.core.data.database.entity.Topic

@Dao
interface SubjectDAO {

    @Query("SELECT * FROM subject_table")
    suspend fun fetchListOfSubject(): List<Subject>
    @Query("SELECT * FROM subject_table WHERE completed = 0")
    suspend fun fetchOnlyNotCompletedSubjects(): List<Subject>

    @Query("DELETE FROM subject_table")
    suspend fun deleteTableSubject()

    @Query("UPDATE subject_table SET list_position = :position WHERE subject_id  = :subjectId")
    suspend fun updateSubjectPosition(position:Int,subjectId: String)

    @Query("UPDATE subject_table SET title = :title, background_color = :backgroundColor,days_of_week = :daysOfWeek WHERE subject_id  = :subjectId")
    suspend fun updateSubject(title: String, backgroundColor: Int, daysOfWeek: List<Int>,subjectId: String)

    @Query("UPDATE subject_table SET completed = :isCompleted WHERE subject_id  = :subjectId")
    suspend fun updateSubjectToggle(isCompleted: Int, subjectId: String)

    @Query("UPDATE subject_table SET selected = :isSelected WHERE subject_id  = :subjectId")
    suspend fun updateSubjectIsSelected(isSelected: Int, subjectId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject)

    @Query("DELETE FROM subject_table WHERE subject_id  = :subjectId")
    suspend fun delete(subjectId: String)

    @Query("SELECT COUNT(subject_id) FROM topic_table WHERE subject_id = :subjectId")
    suspend fun countTopicBySubject(subjectId: String): Int

    @Query("SELECT COUNT(subject_id) FROM topic_table WHERE subject_id = :subjectId AND completed = 1")
    suspend fun countTopicCompletedBySubject(subjectId: String): Int

    @Query("SELECT COUNT(subject_id) FROM topic_table WHERE subject_id = :subjectId AND date = :date")
    suspend fun countTopicBySubjectByDate(subjectId: String, date: String): Int

    @Query("SELECT COUNT(subject_id) FROM topic_table WHERE subject_id = :subjectId AND date = :date AND completed = 1")
    suspend fun countTopicCompletedBySubjectByDate(subjectId: String, date: String): Int

    @Query("SELECT SUM(performance) FROM topic_table WHERE subject_id = :subjectId AND date = :date GROUP BY subject_id  HAVING SUM(performance) > 0")
    suspend fun getPerformanceOfDayBySubject(subjectId: String, date: String): Float

    @Query("SELECT COUNT(subject_id) FROM topic_table WHERE subject_id = :subjectId AND date = :date AND performance > 0")
    suspend fun countIfHavePerformanceByDay(subjectId: String, date: String): Int

    @Query("SELECT COUNT(subject_id) FROM topic_table WHERE subject_id = :subjectId AND performance > 0")
    suspend fun countIfHavePerformance(subjectId: String): Int

    @Query("SELECT SUM(performance) FROM topic_table WHERE subject_id = :subjectId GROUP BY subject_id  HAVING SUM(performance) > 0 ")
    suspend fun getPerformanceBySubject(subjectId: String): Float

    @Query("SELECT COALESCE(SUM(questions),0) FROM topic_table WHERE subject_id = :subjectId")
    suspend fun getNumberOfQuestionsBySubject(subjectId: String): Int

    @Query("SELECT SUM(questions) FROM topic_table WHERE subject_id = :subjectId AND date = :date")
    suspend fun getNumberOfQuestionsOfDaysBySubject(subjectId: String, date: String): Int

    @Query("SELECT * FROM topic_table WHERE subject_id = :subjectId")
    suspend fun fetchAllTopics(subjectId: String): List<Topic>

    @Query("SELECT COALESCE(SUM(questions),0) FROM topic_table")
    suspend fun getAmountOfQuestion(): Int

    @Query("SELECT COALESCE(SUM(matches),0) FROM topic_table")
    suspend fun getAmountOfMatches(): Int


}