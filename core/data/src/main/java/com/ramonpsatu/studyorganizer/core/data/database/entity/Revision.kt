package com.ramonpsatu.studyorganizer.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revision_table")
data class Revision(

    @PrimaryKey
    @ColumnInfo(name = "revision_id") val id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "isCompleted") var isCompleted: Int,
    @ColumnInfo(name = "date_long") val date: Long,
    @ColumnInfo(name = "schedule") val schedule: String,
    @ColumnInfo(name = "date_month") val month: Int,
    @ColumnInfo(name = "date_year") val year: Int

)
