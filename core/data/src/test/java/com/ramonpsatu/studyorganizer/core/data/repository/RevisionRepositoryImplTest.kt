package com.ramonpsatu.studyorganizer.core.data.repository

import com.ramonpsatu.studyorganizer.core.data.database.entity.Revision
import com.ramonpsatu.studyorganizer.core.data.model.RevisionDomain

class RevisionRepositoryImplTest: RevisionRepository {

    private var revisionList = mutableListOf<RevisionDomain>()


    override suspend fun addRevision(revision: Revision) {
        revisionList.add(
            RevisionDomain(
            id = revision.id,
            title = revision.title,
            isCompleted = revision.isCompleted,
            date = revision.date,
            schedule = revision.schedule,
            month =revision.month,
            year = revision.year
        )
        )
    }

    override suspend fun fetchRevisionByMothYear(month: Int, year: Int): List<RevisionDomain> {

        return revisionList.filter {
            it.year == year && it.month == month
        }
    }

    override suspend fun updateRevisionIsSelected(isCompleted: Int, revisionId: String) {
        val index = revisionList.indexOfFirst { it.id == revisionId }

        revisionList[index] = revisionList[index].copy(isCompleted = isCompleted)
    }

    override suspend fun deleteRevision(revisionId: String) {
        revisionList.forEachIndexed { index, revisionDomain ->
            if (revisionDomain.id == revisionId){
                revisionList.removeAt(index)
            }
        }
    }

    override suspend fun deleteRevisionTable() {
        revisionList.clear()
    }

    override suspend fun fetchAllRevision(): List<RevisionDomain> {
       return  revisionList
    }
}