package com.ramonpsatu.studyorganizer.features.collections.model


/**
 * Subject Model representing an Item in a ListView.
 *
 * @param id the id of the Subject
 * @param title the title of the Subject
 * @param isCompleted 1 if the Subject was completed
 * @param numbersOfTopics represents the number topics of the subject
 */
data class SubjectItem(

    val id: String,
    var title: String,
    var isCompleted: Int,
    var numbersOfTopics: Int,
    var numbersOfTopicsCompleted: Int,
    var backgroundColor: Int,
    var daysOfWeek: List<Int>,
    var isSelected: Int,
    var position: Int

)
