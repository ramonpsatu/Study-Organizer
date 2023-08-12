package com.ramonpsatu.studyorganizer.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ramonpsatu.studyorganizer.core.data.database.entity.Revision

@Dao
interface RevisionDAO {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Revision)

    @Query("SELECT * FROM revision_table WHERE date_month = :month AND date_year = :year")
    suspend fun fetchRevisionByMothYear(month: Int,year: Int): List<Revision>
    @Query("SELECT * FROM revision_table")
    suspend fun fetchAllRevision(): List<Revision>


    @Query("UPDATE revision_table SET isCompleted = :isCompleted WHERE revision_id  = :revisionId")
    suspend fun updateRevisionIsSelected(isCompleted: Int, revisionId: String)

    @Query("DELETE FROM revision_table WHERE revision_id  = :revisionId")
    suspend fun deleteRevision(revisionId: String)

    @Query("DELETE FROM revision_table")
    suspend fun deleteRevisionTable()

}