package com.ramonpsatu.studyorganizer.features.collections.model


data class RevisionItem (val id: String,
                         var title: String,
                         var isCompleted: Int,
                         val date: Long,
                         val schedule: String,
                         val month: Int,
                         val year: Int
)