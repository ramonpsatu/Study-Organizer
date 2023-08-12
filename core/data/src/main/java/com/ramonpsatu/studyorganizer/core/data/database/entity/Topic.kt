package com.ramonpsatu.studyorganizer.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ramonpsatu.studyorganizer.core.data.database.entity.Subject

@Entity(
    tableName = "topic_table", foreignKeys = [ForeignKey(
        Subject::class,
        parentColumns = arrayOf("subject_id"),
        childColumns = arrayOf("subject_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Topic(

    @PrimaryKey
    @ColumnInfo(name = "topic_id") val id: String,
    @ColumnInfo(name = "subject_id", index = true) val subjectId: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "completed") var isCompleted: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "topicDescription") var description: String,
    @ColumnInfo(name = "performance") var performance: Float,
    @ColumnInfo(name = "questions") var amountOfQuestions: Int,
    @ColumnInfo(name = "matches") var match: Int,
    @ColumnInfo(name = "error") var error: Int,
    @ColumnInfo(name = "list_position") var position: Int
    )
