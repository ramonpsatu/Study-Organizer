package com.ramonpsatu.studyorganizer.core.data.repository

import android.util.Log
import com.ramonpsatu.studyorganizer.core.data.database.dao.SubjectDAO
import com.ramonpsatu.studyorganizer.core.data.database.entity.Subject
import com.ramonpsatu.studyorganizer.core.data.model.SubjectDomain
import com.ramonpsatu.studyorganizer.core.data.model.TopicDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDAO: SubjectDAO
) : SubjectRepository {


    override suspend fun fetchSubject(): List<SubjectDomain> {


        var listOfSubjects = emptyList<SubjectDomain>()

        withContext(Dispatchers.IO) {

            listOfSubjects = subjectDAO.fetchListOfSubject().map {
                SubjectDomain(
                    id = it.id,
                    title = it.title,
                    isCompleted = it.isCompleted,
                    backgroundColor = it.backgroundColor,
                    daysOfWeek = it.daysOfWeek,
                    isSelected = it.isSelected,
                    position = it.position
                )
            }
        }
        Log.d(LOG_TAG, "List of Subjects returned.")
        return listOfSubjects
    }

    override suspend fun fetchOnlyCompletedSubjects(): List<SubjectDomain> {
        var listOfSubjects = emptyList<SubjectDomain>()

        withContext(Dispatchers.IO) {

            listOfSubjects = subjectDAO.fetchOnlyNotCompletedSubjects().map {
                SubjectDomain(
                    id = it.id,
                    title = it.title,
                    isCompleted = it.isCompleted,
                    backgroundColor = it.backgroundColor,
                    daysOfWeek = it.daysOfWeek,
                    isSelected = it.isSelected,
                    position = it.position
                )
            }
        }
        Log.d(LOG_TAG, "List of Subjects returned.")
        return listOfSubjects
    }


    override suspend fun getSubjectsDaysOfWeek(): List<List<SubjectDomain>> {
        val daysOfeWeekList: MutableList<MutableList<SubjectDomain>> = mutableListOf(
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )

        fetchSubject().forEach {

            for (i in it.daysOfWeek) {

                when (i) {

                    1 -> {
                        daysOfeWeekList[0].add(it.copy())
                    }

                    2 -> {
                        daysOfeWeekList[1].add(it.copy())
                    }

                    3 -> {

                        daysOfeWeekList[2].add(it.copy())
                    }

                    4 -> {

                        daysOfeWeekList[3].add(it.copy())
                    }

                    5 -> {

                        daysOfeWeekList[4].add(it.copy())
                    }

                    6 -> {

                        daysOfeWeekList[5].add(it.copy())
                    }

                    7 -> {

                        daysOfeWeekList[6].add(it.copy())
                    }
                }
            }
        }

        Log.d(LOG_TAG, "Lis of Subjects of the week returned.")
        return daysOfeWeekList
    }

    override suspend fun getNotCompletedSubjectsDaysOfWeek(): List<List<SubjectDomain>> {
        val daysOfeWeekList: MutableList<MutableList<SubjectDomain>> = mutableListOf(
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )

        fetchOnlyCompletedSubjects().forEach {

            for (i in it.daysOfWeek) {

                when (i) {

                    1 -> {
                        daysOfeWeekList[0].add(it.copy())
                    }

                    2 -> {
                        daysOfeWeekList[1].add(it.copy())
                    }

                    3 -> {

                        daysOfeWeekList[2].add(it.copy())
                    }

                    4 -> {

                        daysOfeWeekList[3].add(it.copy())
                    }

                    5 -> {

                        daysOfeWeekList[4].add(it.copy())
                    }

                    6 -> {

                        daysOfeWeekList[5].add(it.copy())
                    }

                    7 -> {

                        daysOfeWeekList[6].add(it.copy())
                    }
                }
            }
        }

        Log.d(LOG_TAG, "Lis of Subjects of the week returned.")
        return daysOfeWeekList
    }


    override suspend fun updateSubject(title: String, backgroundColor: Int, daysOfWeek: List<Int>,subjectId: String) {


        withContext(Dispatchers.IO) {
            subjectDAO.updateSubject(title, backgroundColor, daysOfWeek, subjectId)

        }

        Log.d(LOG_TAG, "Updated subject with ID: $subjectId .")
    }

    override suspend fun updateSubjectPosition(position: Int, subjectId: String) {
        withContext(Dispatchers.IO) {
            subjectDAO.updateSubjectPosition(position, subjectId)

        }

        Log.d(LOG_TAG, "Updated subject position with ID: $subjectId and position $position .")
    }


    override suspend fun delete(id: String) {

        withContext(Dispatchers.IO) {
            subjectDAO.delete(id)
        }

        Log.d(LOG_TAG, "Subject deleted.")
    }

    override suspend fun deleteTableSubject() {
        withContext(Dispatchers.IO) {
            subjectDAO.deleteTableSubject()
        }

        Log.d(LOG_TAG, "The subject table has been cleared!")
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
        val subject = Subject(
            id = id,
            title = title,
            isCompleted = isCompleted,
            backgroundColor = backgroundColor,
            daysOfWeek = daysOfWeek,
            isSelected = isSelected,
            position = position
        )

        withContext(Dispatchers.IO) {
            subjectDAO.insert(subject)

        }

        Log.d(LOG_TAG, "Saved subject : ${subject}.")
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
        val subject = Subject(
            id = id,
            title = title,
            isCompleted = isCompleted,
            backgroundColor = backgroundColor,
            daysOfWeek = daysOfWeek,
            isSelected = isSelected,
            position = position
        )
        withContext(Dispatchers.IO) {

            subjectDAO.insert(subject)
        }

        Log.d(LOG_TAG, "Saved subject coming FireBase : ${subject}.")
    }

    override suspend fun countTopicBySubject(subjectId: String): Int {
        var numberOfTopics: Int

        withContext(Dispatchers.IO) {
            numberOfTopics = subjectDAO.countTopicBySubject(subjectId)
        }

        return numberOfTopics

    }

    override suspend fun countTopicCompletedBySubject(subjectId: String): Int {
        var numberOfTopicsCompleted: Int

        withContext(Dispatchers.IO) {
            numberOfTopicsCompleted = subjectDAO.countTopicCompletedBySubject(subjectId)
        }
        return numberOfTopicsCompleted
    }

    override suspend fun updateSubjectToggle(isCompleted: Int, subjectId: String) {
        withContext(Dispatchers.IO) {
            subjectDAO.updateSubjectToggle(isCompleted, subjectId)
        }

    }

    override suspend fun countTopicBySubjectByDate(subjectId: String, date: String): Int {
        val numberOfTopicsBySubjectByDate: Int

        withContext(Dispatchers.IO) {
            numberOfTopicsBySubjectByDate = subjectDAO.countTopicBySubjectByDate(subjectId, date)
        }
        return numberOfTopicsBySubjectByDate
    }

    override suspend fun countTopicCompletedBySubjectByDate(subjectId: String, date: String): Int {

        val numberOfTopicsCompletedBySubject: Int

        withContext(Dispatchers.IO) {
            numberOfTopicsCompletedBySubject =
                subjectDAO.countTopicCompletedBySubjectByDate(subjectId, date)
        }

        return numberOfTopicsCompletedBySubject
    }

    override suspend fun getPerformanceOfDayBySubject(subjectId: String, date: String): Float {
        var perfByDay: Float = countIfHavePerformanceByDay(subjectId, date).toFloat()
        perfByDay = if (perfByDay > 0) {

            withContext(Dispatchers.IO) {
                subjectDAO.getPerformanceOfDayBySubject(
                    subjectId,
                    date
                ) / countIfHavePerformanceByDay(
                    subjectId,
                    date
                )
            }

        } else {
            0f
        }


        return perfByDay
    }

    override suspend fun getPerformanceBySubject(subjectId: String): Float {
        var perf: Float = countIfHavePerformance(subjectId).toFloat()
        perf = if (perf > 0) {

            withContext(Dispatchers.IO) {
                (subjectDAO.getPerformanceBySubject(subjectId) / countIfHavePerformance(subjectId))
            }


        } else {
            0f
        }

        return perf
    }

    override suspend fun getNumberOfQuestionsBySubject(subjectId: String): Int {

        val numberOfQuestions: Int

        withContext(Dispatchers.IO) {
            numberOfQuestions = subjectDAO.getNumberOfQuestionsBySubject(subjectId)
        }

        return numberOfQuestions
    }

    override suspend fun getNumberOfQuestionsOfDaysBySubject(subjectId: String, date: String): Int {
        val numberOfQuestionsOfDay: Int

        withContext(Dispatchers.IO) {
            numberOfQuestionsOfDay = subjectDAO.getNumberOfQuestionsOfDaysBySubject(subjectId, date)
        }

        return numberOfQuestionsOfDay
    }

    override suspend fun countIfHavePerformance(subjectId: String): Int {

        val numberPerformance: Int

        withContext(Dispatchers.IO) {
            numberPerformance = subjectDAO.countIfHavePerformance(subjectId)
        }


        return numberPerformance
    }

    override suspend fun countIfHavePerformanceByDay(subjectId: String, date: String): Int {

        val numberPerformanceByDay: Int

        withContext(Dispatchers.IO) {
            numberPerformanceByDay = subjectDAO.countIfHavePerformanceByDay(subjectId, date)
        }


        return numberPerformanceByDay
    }

    override suspend fun fetchAllTopics(subjectId: String): List<TopicDomain> {
        var list = emptyList<TopicDomain>()

        withContext(Dispatchers.IO) {
            list = subjectDAO.fetchAllTopics(subjectId).map { topic ->
                TopicDomain(
                    id = topic.id,
                    subjectId = topic.subjectId,
                    title = topic.title,
                    isCompleted = topic.isCompleted,
                    date = topic.date,
                    description = topic.description,
                    performance = topic.performance,
                    amountOfQuestions = topic.amountOfQuestions,
                    match = topic.match,
                    error = topic.error,
                    position = topic.position
                )
            }
        }
        return list
    }

    override suspend fun updateSubjectIsSelected(isSelected: Int, subjectId: String) {

        withContext(Dispatchers.IO) {
            subjectDAO.updateSubjectIsSelected(isSelected, subjectId)
        }

        Log.d(LOG_TAG, "Updated subject: $subjectId , $isSelected .")
    }

    override suspend fun getAmountOfQuestion(): Int {

        var amount: Int

        withContext(Dispatchers.IO) {
            amount = subjectDAO.getAmountOfQuestion()
        }

        return amount
    }

    override suspend fun getAmountOfMatches(): Int {
        var amount: Int

        withContext(Dispatchers.IO) {
            amount = subjectDAO.getAmountOfMatches()
        }

        return amount
    }


    private companion object {

        private const val LOG_TAG = "SubjectRepository"
    }
}