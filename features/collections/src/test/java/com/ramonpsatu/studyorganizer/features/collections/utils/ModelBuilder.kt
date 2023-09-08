package com.ramonpsatu.studyorganizer.features.collections.utils

import android.graphics.Color
import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import java.util.UUID

class ModelBuilder{

    lateinit var subject: SubjectItem
    lateinit var topicItem: TopicItem

    fun subjectBuilder(): SubjectItem {
    subject =  SubjectItem(
        id = UUID.randomUUID().toString(),
        title = UUID.randomUUID().toString().substring(0..1),
        isCompleted = 0,
        numbersOfTopics = 0,
        numbersOfTopicsCompleted = 0,
        backgroundColor = Color.GRAY,
        daysOfWeek = listOf(0, 3, 4),
        isSelected = 0,
        position = 0
    )
        return subject
    }

    fun topicBuilder(): TopicItem {
        topicItem =  TopicItem(
            id = UUID.randomUUID().toString(),
            subjectId = subject.id,
            title = UUID.randomUUID().toString().substring(0..1),
            isCompleted = 0,
            date = "07/09/2023",
            description = "topic-description",
            performance = 0f,
            amountOfQuestions = 0,
            match = 0,
            error = 0,
            position = 0
        )
        return topicItem
    }
}