package com.ramonpsatu.studyorganizer.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_registration")
data class OffLineRegistration(

    @PrimaryKey
    @ColumnInfo(name = "itemId")val id: String,
    @ColumnInfo(name = "type") val itemType:Int
)
