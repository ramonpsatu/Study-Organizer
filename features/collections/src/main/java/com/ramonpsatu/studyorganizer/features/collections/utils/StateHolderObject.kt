package com.ramonpsatu.studyorganizer.features.collections.utils

import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem

object StateHolderObject {

    var turn_flag_SaveOrUpdate = false
    var dateTodayForTopic = ""
    var dayOfWeek = 0
    var versionCodeName = ""

    var currentNavbarButton = 0
    var configurationFragmentFlag = true
    var configurationFragmentSwitch = true
    var flagAccountDetailsTextChange = 0
    var emailToEdit = "email..."
    var userHaveBeenDeleted = false


    private var id: String = ""
    private var title: String = ""
    private var isCompleted: Int = 0
    private var numbersOfTopics: Int = 0
    private var numbersOfTopicsCompleted: Int = 0
    private var backgroundColor: Int = 0
    private var daysOfWeek: List<Int> = emptyList()
    private var isSelected = 0
    private var position = 0


    private var idTopic: String = ""
    private var subjectId: String = ""
    private var titleTopic: String = ""
    private var isCompletedTopic: Int = 0
    private var dateTopic: String = ""
    private var description: String = ""
    private var performance: Float = 0f
    private var amountOfQuestions: Int = 0
    private var match: Int = 0
    private var error: Int = 0
    private var positionTopic = 0


    fun setAttributesSubjectItem(subjectItem: SubjectItem): SubjectItem {
        val subject = SubjectItem(
            id = subjectItem.id,
            title = subjectItem.title,
            isCompleted = subjectItem.isCompleted,
            numbersOfTopics = subjectItem.numbersOfTopics,
            numbersOfTopicsCompleted = subjectItem.numbersOfTopicsCompleted,
            backgroundColor = subjectItem.backgroundColor,
            daysOfWeek = subjectItem.daysOfWeek,
            isSelected = subjectItem.isSelected,
            position = subjectItem.position
        )

        id = subject.id
        title = subject.title
        isCompleted = subject.isCompleted
        numbersOfTopics = subject.numbersOfTopics
        numbersOfTopicsCompleted = subject.numbersOfTopicsCompleted
        backgroundColor = subject.backgroundColor
        daysOfWeek = subject.daysOfWeek
        isSelected = subject.isSelected
        position = subject.position


        return subject
    }

    fun safeArgsSubjectItem(): SubjectItem {
        return SubjectItem(
            id,
            title,
            isCompleted,
            numbersOfTopics,
            numbersOfTopicsCompleted,
            backgroundColor,
            daysOfWeek,
            isSelected,
            position
        )
    }

    fun setTopicDTO(topicItem: TopicItem): TopicItem {
        val topic = TopicItem(
            id = topicItem.id,
            subjectId = topicItem.subjectId,
            title = topicItem.title,
            isCompleted = topicItem.isCompleted,
            date = topicItem.date,
            description = topicItem.description,
            performance = topicItem.performance,
            amountOfQuestions = topicItem.amountOfQuestions,
            match = topicItem.match,
            error = topicItem.error,
            position = topicItem.position
        )

        idTopic = topic.id
        subjectId = topic.subjectId
        titleTopic = topic.title
        isCompletedTopic = topic.isCompleted
        dateTopic = topic.date
        description = topic.description
        performance = topic.performance
        amountOfQuestions = topic.amountOfQuestions
        match = topic.match
        error = topic.error
        positionTopic = topic.position

        return topic
    }

    fun getTopicDTO(): TopicItem {
        return TopicItem(
            idTopic,
            subjectId,
            titleTopic,
            isCompletedTopic,
            dateTopic,
            description,
            performance,
            amountOfQuestions,
            match,
            error,
            positionTopic
        )
    }
}