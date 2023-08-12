package com.ramonpsatu.studyorganizer.core.data.model


data class TopicDomain(

    val id: String,
    val subjectId: String,
    var title: String,
    var isCompleted: Int,
    val date: String,
    var description: String,
    var performance: Float,
    var amountOfQuestions: Int,
    var match: Int,
    var error: Int,
    var position:Int
)
