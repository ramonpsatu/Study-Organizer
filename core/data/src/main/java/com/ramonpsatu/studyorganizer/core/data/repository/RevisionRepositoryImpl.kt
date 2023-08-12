package com.ramonpsatu.studyorganizer.core.data.repository

import android.util.Log
import com.ramonpsatu.studyorganizer.core.data.database.dao.RevisionDAO
import com.ramonpsatu.studyorganizer.core.data.database.entity.Revision
import com.ramonpsatu.studyorganizer.core.data.model.RevisionDomain
import com.ramonpsatu.studyorganizer.core.data.repository.RevisionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RevisionRepositoryImpl @Inject constructor(private val revisionDAO: RevisionDAO) :
    RevisionRepository {

    private companion object {
        const val LOG_TAG = "RevisionRepository"
    }

    override suspend fun addRevision(revision: Revision) {
        withContext(Dispatchers.IO){
            revisionDAO.insert(revision)
        }
    }

    override suspend fun fetchRevisionByMothYear(month: Int, year: Int): List<RevisionDomain> {
        Log.d(LOG_TAG, "List of revisions returned.")
        var resultList = listOf<RevisionDomain>()
        withContext(Dispatchers.IO){

           resultList = revisionDAO.fetchRevisionByMothYear(month, year).map {revision->

                RevisionDomain(
                    id = revision.id,
                    title = revision.title,
                    isCompleted = revision.isCompleted,
                    date = revision.date,
                    schedule = revision.schedule,
                    month =revision.month,
                    year = revision.year
                )


            }.sortedBy { it.date }

        }

        return  resultList
    }

    override suspend fun updateRevisionIsSelected(isCompleted: Int, revisionId: String) {
        withContext(Dispatchers.IO){
            revisionDAO.updateRevisionIsSelected(isCompleted, revisionId)
        }
        Log.d(LOG_TAG, "Updated revision: $revisionId , $isCompleted .")
    }

    override suspend fun deleteRevision(revisionId: String) {
        withContext(Dispatchers.IO){
          revisionDAO.deleteRevision(revisionId)
        }
        Log.d(LOG_TAG, "Deleted revision: $revisionId.")
    }

    override suspend fun deleteRevisionTable() {
        withContext(Dispatchers.IO){
            revisionDAO.deleteRevisionTable()
        }
        Log.d(LOG_TAG, "Clean revision table.")
    }

    override suspend fun fetchAllRevision(): List<RevisionDomain> {
        Log.d(LOG_TAG, "List of revisions returned.")
        var resultList = listOf<RevisionDomain>()
        withContext(Dispatchers.IO){

            resultList = revisionDAO.fetchAllRevision().map {revision->

                RevisionDomain(
                    id = revision.id,
                    title = revision.title,
                    isCompleted = revision.isCompleted,
                    date = revision.date,
                    schedule = revision.schedule,
                    month =revision.month,
                    year = revision.year
                )


            }

        }

        return  resultList
    }

}
