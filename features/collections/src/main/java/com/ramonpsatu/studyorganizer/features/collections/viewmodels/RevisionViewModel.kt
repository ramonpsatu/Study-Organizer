package com.ramonpsatu.studyorganizer.features.collections.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ramonpsatu.studyorganizer.features.collections.model.RevisionItem
import com.ramonpsatu.studyorganizer.core.data.database.entity.Revision
import com.ramonpsatu.studyorganizer.core.data.repository.RevisionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RevisionViewModel @Inject constructor(
    private val revisionRepository: RevisionRepository

    ) : ViewModel() {

    data class UiStateRevision(val revisionList: List<RevisionItem>)

    private val _revision: MutableLiveData<UiStateRevision> by lazy {

        MutableLiveData<UiStateRevision>(UiStateRevision(revisionList = emptyList()))

    }

    fun showStateOnce(): LiveData<UiStateRevision> = _revision

    fun getAttributesRevision(itemPosition:Int): RevisionItem {
        return _revision.value!!.revisionList[itemPosition]
    }

    suspend fun  refreshUiStateRevision(month: Int, year: Int) {
        val resultList = revisionRepository.fetchRevisionByMothYear(month, year)

        _revision.postValue(UiStateRevision(resultList.map { revision ->
                RevisionItem(
                    id = revision.id,
                    title = revision.title,
                    isCompleted = revision.isCompleted,
                    date = revision.date,
                    schedule = revision.schedule,
                    month = revision.month,
                    year = revision.year
                )
            }.sortedBy { it.isCompleted }))

    }

    suspend fun addRevisionInLocal(revision: Revision) {
        revisionRepository.addRevision(revision)
    }

    suspend fun deleteRevisionInLocal(revisionId: String){
        revisionRepository.deleteRevision(revisionId)
    }

    suspend fun toggleRevisionInLocal(isCompleted: Int, revisionId: String){
            revisionRepository.updateRevisionIsSelected(isCompleted, revisionId)
    }
}