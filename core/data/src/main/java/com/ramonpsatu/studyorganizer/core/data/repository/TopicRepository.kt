package com.ramonpsatu.studyorganizer.core.data.repository


import com.ramonpsatu.studyorganizer.core.data.model.TopicDomain

interface TopicRepository {

    suspend fun fetchListOfTopics(): List<TopicDomain>
    suspend fun fetchTopic(subjectId: String, date: String): List<TopicDomain>
    suspend fun fetchAllTopics(subjectId: String): List<TopicDomain>

    suspend fun delete(topicId: String)

    suspend fun updateTopic(topicId: String, title: String, description: String)


    suspend fun updatePerformance(
        topicId: String,
        performance: Float,
        amountQuestions: Int,
        match: Int,
        error: Int
    )

    suspend fun updateToggleTopic(isCompleted: Int, topicId: String)

    suspend fun updateTopicPosition(position:Int, topicId: String)

    suspend fun addTopic(
        id:String,
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
    )

    suspend fun countTopics(subjectId: String, date: String): Int


    suspend fun countTopicsCompleted(subjectId: String, date: String): Int
}