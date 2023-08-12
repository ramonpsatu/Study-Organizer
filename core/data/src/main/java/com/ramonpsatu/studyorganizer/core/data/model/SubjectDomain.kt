package com.ramonpsatu.studyorganizer.core.data.model

data class SubjectDomain(

    val id: String,
    var title: String,
    var isCompleted: Int,
    var backgroundColor: Int,
    var daysOfWeek: List<Int>,
    var isSelected: Int,
    var position: Int

)
