package com.ramonpsatu.studyorganizer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ramonpsatu.studyorganizer.core.data.database.entity.OffLineRegistration

@Dao
interface OffLineRegistrationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: OffLineRegistration)

    @Query("SELECT * FROM offline_registration ")
    suspend fun fetchRegister(): List<OffLineRegistration>

    @Query("DELETE FROM offline_registration")
    suspend fun deleteRegister()
}