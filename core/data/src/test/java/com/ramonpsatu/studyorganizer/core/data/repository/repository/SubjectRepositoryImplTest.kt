package com.ramonpsatu.studyorganizer.core.data.repository.repository

import android.graphics.Color
import android.util.Log
import com.ramonpsatu.studyorganizer.core.data.model.SubjectDomain
import com.ramonpsatu.studyorganizer.core.data.model.TopicDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class SubjectRepositoryImplTest : SubjectRepositoryTest {

    override suspend fun fetchSubject(): List<SubjectDomain> {
        val listOfSubjects = mutableListOf<SubjectDomain>()

        withContext(Dispatchers.IO) {

            for (index in 0 ..5){

                listOfSubjects.add(SubjectDomain(
                    id = UUID.randomUUID().toString(),
                    title = "Subject $index",
                    isCompleted = 0,
                    backgroundColor = Color.BLUE,
                    daysOfWeek = listOf(0,1,3),
                    isSelected = 0,
                    position = index,
                ))
            }
        }
        Log.d("SubjectRepositoryImplTest", "List of Subjects returned.")
        return listOfSubjects
    }

    override suspend fun fetchOnlyCompletedSubjects(): List<SubjectDomain> {
        TODO("Not yet implemented")
    }

    override suspend fun getSubjectsDaysOfWeek(): List<List<SubjectDomain>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotCompletedSubjectsDaysOfWeek(): List<List<SubjectDomain>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSubject(
        title: String,
        backgroundColor: Int,
        daysOfWeek: List<Int>,
        subjectId: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSubjectPosition(position: Int, subjectId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTableSubject() {
        TODO("Not yet implemented")
    }

    override suspend fun addSubject(
        id: String,
        title: String,
        isCompleted: Int,
        backgroundColor: Int,
        daysOfWeek: List<Int>,
        isSelected: Int,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun addSubjectComingFireBase(
        id: String,
        title: String,
        isCompleted: Int,
        backgroundColor: Int,
        daysOfWeek: List<Int>,
        isSelected: Int,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun countTopicBySubject(subjectId: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countTopicCompletedBySubject(subjectId: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateSubjectToggle(isCompleted: Int, subjectId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun countTopicBySubjectByDate(subjectId: String, date: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countTopicCompletedBySubjectByDate(subjectId: String, date: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getPerformanceOfDayBySubject(subjectId: String, date: String): Float {
        TODO("Not yet implemented")
    }

    override suspend fun getPerformanceBySubject(subjectId: String): Float {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfQuestionsBySubject(subjectId: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getNumberOfQuestionsOfDaysBySubject(subjectId: String, date: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countIfHavePerformance(subjectId: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countIfHavePerformanceByDay(subjectId: String, date: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllTopics(subjectId: String): List<TopicDomain> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSubjectIsSelected(isSelected: Int, subjectId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getAmountOfQuestion(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAmountOfMatches(): Int {
        TODO("Not yet implemented")
    }
}