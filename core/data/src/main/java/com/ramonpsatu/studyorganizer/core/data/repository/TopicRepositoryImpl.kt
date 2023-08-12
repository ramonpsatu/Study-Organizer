package com.ramonpsatu.studyorganizer.core.data.repository

import android.util.Log
import com.ramonpsatu.studyorganizer.core.data.database.dao.TopicDAO
import com.ramonpsatu.studyorganizer.core.data.database.entity.Topic
import com.ramonpsatu.studyorganizer.core.data.model.TopicDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val topicDAO: TopicDAO
) : TopicRepository {
    override suspend fun fetchListOfTopics(): List<TopicDomain> {
        var listOfTopics = emptyList<TopicDomain>()

        withContext(Dispatchers.IO) {
            listOfTopics = topicDAO.fetchListOfTopics().map { topic ->
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
        Log.d(LOG_TAG, "List of Topics returned!")
        return listOfTopics
    }


    override suspend fun fetchTopic(subjectId: String, date: String): List<TopicDomain> {


        var listOfTopics = emptyList<TopicDomain>()

        withContext(Dispatchers.IO) {
            listOfTopics = topicDAO.fetchTopics(subjectId, date).map { topic ->
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
        Log.d(LOG_TAG, "List of Topics returned!")
        return listOfTopics
    }

    override suspend fun fetchAllTopics(subjectId: String): List<TopicDomain> {
        var listOfTopics = emptyList<TopicDomain>()

        withContext(Dispatchers.IO) {
            listOfTopics = topicDAO.fetchAllTopics(subjectId).map { topic ->
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
        Log.d(LOG_TAG, "List of Topics returned!")
        return listOfTopics
    }

    override suspend fun delete(topicId: String) {
        withContext(Dispatchers.IO) {
            topicDAO.delete(topicId)
        }
        Log.d(LOG_TAG, "Topic with id:$topicId have been deleted!")
    }


    override suspend fun updateTopic(topicId: String, title: String, description: String) {
        withContext(Dispatchers.IO) {
            topicDAO.update(topicId, title, description)
        }

        Log.d(LOG_TAG, "Updated topic: $topicId.")
    }

    override suspend fun updatePerformance(
        topicId: String,
        performance: Float,
        amountQuestions: Int,
        match: Int,
        error: Int
    ) {


        withContext(Dispatchers.IO) {
            topicDAO.updatePerformance(topicId, performance, amountQuestions, match, error)

        }
        Log.d(LOG_TAG, "Recorded topic performance: $topicId , Performance: $performance.")
    }

    override suspend fun updateToggleTopic(isCompleted: Int, topicId: String) {


        withContext(Dispatchers.IO) {
            topicDAO.updateToggle(isCompleted, topicId)
        }

        Log.d(LOG_TAG, "Topic with id:$topicId have been toggled! IsCompleted:$isCompleted")
    }

    override suspend fun updateTopicPosition(position: Int, topicId: String) {
        withContext(Dispatchers.IO) {
            topicDAO.updateTopicPosition(position, topicId)

        }

        Log.d(LOG_TAG, "Updated topic position with ID: $topicId and position $position .")
    }

    override suspend fun addTopic(
        id: String,
        subjectId: String,
        title: String,
        isCompleted: Int,
        date: String,
        description: String,
        performance: Float,
        amountOfQuestions: Int,
        match: Int,
        error: Int,
        position:Int
    ) {
        val topic = Topic(
            id = id,
            subjectId = subjectId,
            title = title,
            isCompleted = isCompleted,
            date = date,
            description = description,
            performance = performance,
            amountOfQuestions = amountOfQuestions,
            match = match,
            error = error,
            position = position

        )

        withContext(Dispatchers.IO) {
            topicDAO.insert(topic)
        }
        Log.d(LOG_TAG, "Saved topic : ${topic}.")
    }


    override suspend fun countTopics(subjectId: String, date: String): Int {

        var amountTopics: Int

        withContext(Dispatchers.IO) {
            amountTopics = topicDAO.countTopics(subjectId, date)
        }

        return amountTopics
    }

    override suspend fun countTopicsCompleted(subjectId: String, date: String): Int {
        var amountTopicsCompleted: Int

        withContext(Dispatchers.IO) {
            amountTopicsCompleted = topicDAO.countTopicsCompleted(subjectId, date)
        }

        return amountTopicsCompleted
    }


    private companion object {

        private const val LOG_TAG = "TopicRepository"
    }

}