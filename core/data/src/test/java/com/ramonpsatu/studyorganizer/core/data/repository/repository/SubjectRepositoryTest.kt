package com.ramonpsatu.studyorganizer.core.data.repository.repository


import com.ramonpsatu.studyorganizer.core.data.model.SubjectDomain
import com.ramonpsatu.studyorganizer.core.data.model.TopicDomain

interface SubjectRepositoryTest {


    suspend fun fetchSubject(): List<SubjectDomain>
    suspend fun fetchOnlyCompletedSubjects(): List<SubjectDomain>

    suspend fun getSubjectsDaysOfWeek(): List<List<SubjectDomain>>
    suspend fun getNotCompletedSubjectsDaysOfWeek(): List<List<SubjectDomain>>

    suspend fun updateSubject(title: String, backgroundColor: Int, daysOfWeek: List<Int>,subjectId: String)
    suspend fun updateSubjectPosition(position:Int,subjectId: String)

    suspend fun delete(id: String)
    suspend fun deleteTableSubject()
    suspend fun addSubject(
     id: String, title: String, isCompleted: Int,
        backgroundColor: Int, daysOfWeek: List<Int>, isSelected: Int,position: Int
    )

    suspend fun addSubjectComingFireBase(
        id:String, title: String, isCompleted: Int,
        backgroundColor: Int, daysOfWeek: List<Int>, isSelected: Int,  position: Int
    )
    suspend fun countTopicBySubject(subjectId: String): Int

    suspend fun countTopicCompletedBySubject(subjectId: String): Int

    suspend fun updateSubjectToggle(isCompleted: Int, subjectId: String)

    suspend fun countTopicBySubjectByDate(subjectId: String, date: String): Int

    suspend fun countTopicCompletedBySubjectByDate(subjectId: String, date: String): Int

    suspend fun getPerformanceOfDayBySubject(subjectId: String, date: String): Float

    suspend fun getPerformanceBySubject(subjectId: String): Float

    suspend fun getNumberOfQuestionsBySubject(subjectId: String): Int

    suspend fun getNumberOfQuestionsOfDaysBySubject(subjectId: String, date: String): Int

    suspend fun countIfHavePerformance(subjectId: String): Int
    suspend fun countIfHavePerformanceByDay(subjectId: String, date: String): Int

    suspend fun fetchAllTopics(subjectId: String): List<TopicDomain>

    suspend fun updateSubjectIsSelected(isSelected: Int, subjectId: String)

    suspend fun getAmountOfQuestion(): Int
    suspend fun getAmountOfMatches(): Int
}