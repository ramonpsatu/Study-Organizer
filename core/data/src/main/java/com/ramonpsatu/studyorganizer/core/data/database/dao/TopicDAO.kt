package com.ramonpsatu.studyorganizer.core.data.database.dao

import androidx.room.*
import com.ramonpsatu.studyorganizer.core.data.database.entity.Topic

@Dao
interface TopicDAO {
    @Query("SELECT * FROM topic_table")
    suspend fun fetchListOfTopics(): List<Topic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(topic: Topic)

    @Query("SELECT * FROM topic_table WHERE subject_id = :subjectId AND date = :date")
    suspend fun fetchTopics(subjectId: String, date: String): List<Topic>

    @Query("SELECT * FROM topic_table WHERE subject_id = :subjectId")
    suspend fun fetchAllTopics(subjectId: String): List<Topic>

    @Query("DELETE FROM topic_table WHERE topic_id = :topicId")
    suspend fun delete(topicId: String)

    @Query("UPDATE topic_table SET title =:title, topicDescription =:description  WHERE topic_id=:topicId")
    suspend fun update(topicId: String, title: String, description: String)

    @Query("UPDATE topic_table SET performance =:performance,questions =:amountQuestions,matches=:match,error =:error  WHERE topic_id=:topicId")
    suspend fun updatePerformance(
        topicId: String,
        performance: Float,
        amountQuestions: Int,
        match: Int,
        error: Int
    )

    @Query("UPDATE topic_table SET list_position = :position WHERE topic_id  = :topicId")
    suspend fun updateTopicPosition(position:Int, topicId: String)
    @Query("UPDATE topic_table SET completed =:isCompleted WHERE topic_id=:topicId")
    suspend fun updateToggle(isCompleted: Int, topicId: String)

    @Query("SELECT COUNT(topic_id) FROM topic_table WHERE subject_id = :subjectId AND date = :date")
    suspend fun countTopics(subjectId: String, date: String): Int

    @Query("SELECT COUNT(subject_id) FROM topic_table WHERE subject_id = :subjectId AND completed = 1 AND date = :date")
    suspend fun countTopicsCompleted(subjectId: String, date: String): Int
}