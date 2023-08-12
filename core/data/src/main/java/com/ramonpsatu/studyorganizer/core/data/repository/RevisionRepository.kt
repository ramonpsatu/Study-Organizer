package com.ramonpsatu.studyorganizer.core.data.repository

import com.ramonpsatu.studyorganizer.core.data.database.entity.Revision
import com.ramonpsatu.studyorganizer.core.data.model.RevisionDomain

interface RevisionRepository {



    suspend fun addRevision(revision: Revision)

    suspend fun fetchRevisionByMothYear(month: Int,year: Int): List<RevisionDomain>


    suspend fun updateRevisionIsSelected(isCompleted: Int, revisionId: String)

    suspend fun deleteRevision(revisionId: String)
    suspend fun deleteRevisionTable()

    suspend fun fetchAllRevision(): List<RevisionDomain>
}