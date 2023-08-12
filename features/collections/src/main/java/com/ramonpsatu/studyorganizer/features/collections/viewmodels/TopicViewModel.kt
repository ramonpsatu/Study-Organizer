package com.ramonpsatu.studyorganizer.features.collections.viewmodels

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramonpsatu.studyorganizer.core.data.repository.TopicRepository
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllTopicsOfTheOneSubjectUseCase
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: TopicRepository,
    private val getAllTopicsOfTheOneSubjectUseCase: GetAllTopicsOfTheOneSubjectUseCase
) : ViewModel() {

    private val uiState: MutableLiveData<UiStateTopic> by lazy {

        MutableLiveData<UiStateTopic>(UiStateTopic(newTopicItem = emptyList()))

    }

    private val uiStateAllTopicsBySubjects: MutableLiveData<UiStateTopicsBySubject> by lazy {

        MutableLiveData<UiStateTopicsBySubject>(UiStateTopicsBySubject(allTopicsBySubject = emptyList()))

    }


    fun stateOnce(): LiveData<UiStateTopic> = uiState

    fun stateOnceAllTopicsBySubject(): LiveData<UiStateTopicsBySubject> = uiStateAllTopicsBySubjects

    fun getAttributesOfTopics(index: Int): TopicItem {
        return uiState.value!!.newTopicItem[index]
    }

    fun getTopicListSize(): Int {

        if (uiState.value!!.newTopicItem.isEmpty()) {
            return 0
        }

        return uiState.value!!.newTopicItem.size
    }

    fun getAttributesTopics(index: Int): TopicItem {
        return uiStateAllTopicsBySubjects.value!!.allTopicsBySubject[index]
    }
    fun research(editText: EditText, listOfTopicItem: List<TopicItem>): List<TopicItem> {


        if (editText.isFocused) {

            val resultLis = mutableListOf<TopicItem>()
            var editTextField = editText.text.toString()

            if (editTextField.length > 10) {
                editTextField = editTextField.substring(0..10)
            }


            for (item in listOfTopicItem) {

                if (item.title.contains(editTextField)) {
                    resultLis.add(item)
                }
            }



            return resultLis
        }


        return emptyList()
    }



    fun updateTopicPosition(position: Int, topicId: String) {

        viewModelScope.launch {
            repository.updateTopicPosition(position, topicId)
        }

    }


    suspend fun getNumbersOfTopics(subjectId: String, date: String): Int {
        return repository.countTopics(subjectId, date)
    }

    suspend fun getNumbersOfTopicsCompleted(subjectId: String, date: String): Int {
        return repository.countTopicsCompleted(subjectId, date)
    }


    suspend fun addTopic(
        id: String,
        subjectId: String, title: String, isCompleted: Int,
        date: String, description: String, performance: Float,
        amountOfQuestions: Int, match: Int, error: Int,
        position: Int
    ) {
        repository.addTopic(
            id,
            subjectId,
            title, isCompleted,
            date, description,
            performance,
            amountOfQuestions,
            match, error, position
        )

        refreshUiStateOfTopic(subjectId, date)


    }

    suspend fun updatePerformance(
        topicId: String,
        performance: Float,
        amountQuestions: Int,
        match: Int, error: Int
    ) {

        repository.updatePerformance(topicId, performance, amountQuestions, match, error)
        refreshUiStateOfTopic(
            StateHolderObject.safeArgsSubjectItem().id,
            StateHolderObject.dateTodayForTopic
        )
    }

    suspend fun update(topicId: String, title: String, description: String) {

        repository.updateTopic(topicId, title, description)
        refreshUiStateOfTopic(
            StateHolderObject.safeArgsSubjectItem().id,
            StateHolderObject.dateTodayForTopic
        )

    }

    suspend fun updateTopicToggle(isCompleted: Int, topicId: String) {
        repository.updateToggleTopic(isCompleted, topicId)
    }

    suspend fun delete(topicId: String) {
        repository.delete(topicId)
    }


    suspend fun refreshUiStateOfTopic(subjectId: String, date: String) {
        uiState.postValue(
            UiStateTopic(
                getAllTopicsOfTheOneSubjectUseCase(subjectId, date).sortedBy { it.isCompleted })
        )
    }

    suspend fun refreshUiStateAllTopicsBySubject(subjectId: String) {
        uiStateAllTopicsBySubjects.postValue(
            UiStateTopicsBySubject(
                repository.fetchAllTopics(subjectId).map {

                    TopicItem(
                        id = it.id,
                        subjectId = it.subjectId,
                        title = it.title,
                        isCompleted = it.isCompleted,
                        date = it.date,
                        description = it.description,
                        performance = it.performance,
                        amountOfQuestions = it.amountOfQuestions,
                        match = it.match,
                        error = it.error,
                        position = it.position
                    )
                }.sortedBy { it.date.toInt() })
        )
    }

    data class UiStateTopic(val newTopicItem: List<TopicItem>)
    data class UiStateTopicsBySubject(val allTopicsBySubject: List<TopicItem>)

}