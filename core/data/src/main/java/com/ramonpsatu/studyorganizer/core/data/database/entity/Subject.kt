package com.ramonpsatu.studyorganizer.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ramonpsatu.studyorganizer.core.data.database.DaysOfWeekConverter

@Entity(tableName = "subject_table")
data class Subject(

    @PrimaryKey
    @ColumnInfo(name = "subject_id") val id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "completed") var isCompleted: Int,
    @ColumnInfo(name = "background_color") var backgroundColor: Int,
    @TypeConverters(DaysOfWeekConverter::class)
    @ColumnInfo(name = "days_of_week") var daysOfWeek: List<Int>,
    @ColumnInfo(name = "selected") var isSelected: Int,
    @ColumnInfo(name = "list_position") var position: Int



)
