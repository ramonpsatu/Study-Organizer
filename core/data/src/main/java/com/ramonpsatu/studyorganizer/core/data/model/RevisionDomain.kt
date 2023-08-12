package com.ramonpsatu.studyorganizer.core.data.model


data class RevisionDomain(
    val id: String,
    var title: String,
    var isCompleted: Int,
    val date: Long,
    val schedule: String,
    val month: Int,
    val year: Int
)