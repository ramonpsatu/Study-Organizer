package com.ramonpsatu.studyorganizer.features.collections.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramonpsatu.studyorganizer.core.data.repository.CalendarRepository
import com.ramonpsatu.studyorganizer.core.data.repository.SubjectRepository
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectUseCase
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectsDaysOfWeekUseCase
import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectListViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val getAllSubjectUseCase: GetAllSubjectUseCase,
    private val getAllSubjectsDaysOfWeekUseCase: GetAllSubjectsDaysOfWeekUseCase,
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    data class DatesState(val daysOfWeek: List<Int>, val datesOfWeek: List<Long>)
    data class UiState(val subjectItemList: List<SubjectItem>)
    data class UiStateWeek(val subjectItemWeek: List<List<SubjectItem>>)

    private val uiState: MutableLiveData<UiState> by lazy {

        MutableLiveData<UiState>(UiState(subjectItemList = emptyList()))

    }

    private var daysOfWeek = mutableListOf(0, 1, 2, 3, 4, 5, 6)
    private var datesOfWeek: MutableList<Long> = mutableListOf(0, 1, 2, 3, 4, 5, 6)

    private val datesStateState: MutableLiveData<DatesState> by lazy {

        MutableLiveData<DatesState>(DatesState(daysOfWeek = daysOfWeek, datesOfWeek = datesOfWeek))

    }

    private var subjectDaysOfWeekList: List<List<SubjectItem>> = mutableListOf(
        emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
        emptyList()
    )

    private val uiStateDaysOfWeek: MutableLiveData<UiStateWeek> by lazy {

        MutableLiveData<UiStateWeek>(UiStateWeek(subjectItemWeek = subjectDaysOfWeekList))

    }

    fun getSubjectListSize(): Int {

        if (uiState.value!!.subjectItemList.isEmpty()){
            return 0
        }

        return uiState.value!!.subjectItemList.size
    }

    fun getCompletedSubjectListSize(): Int {
        var size = 0
        for (index in 0 until uiState.value!!.subjectItemList.size) {
            if (uiState.value!!.subjectItemList[index].isCompleted == 1) {
                size++
            }
        }

        return size
    }

    fun stateOnceAndDaysOfWeek(): LiveData<UiStateWeek> = uiStateDaysOfWeek

    fun stateOnceDates(): LiveData<DatesState> = datesStateState

    fun stateOnceAndStream(): LiveData<UiState> = uiState

    fun onResume() {
        viewModelScope.launch {
            refreshUiState()
        }
    }

   suspend fun  getPerformanceTotal(): String {
        val amountQuestions = getAmountOfQuestion().toFloat()
        val amountMatches = subjectRepository.getAmountOfMatches().toFloat()
        val minus =  (amountMatches / amountQuestions)
        val result = minus*100
        var string =String.format("%.0f", result) + "%"
       if (string.contains("NaN")){
           string = "0"
       }
        return string
    }

    fun addSubject(
        id: String,
        title: String,
        isCompleted: Int,
        backgroundColor: Int,
        daysOfWeek: List<Int>,
        isSelected: Int,
        position: Int
    ) {
        viewModelScope.launch {
            subjectRepository.addSubject(
                id,
                title, isCompleted,  backgroundColor,
                daysOfWeek, isSelected, position


            )
            refreshUiState()
            refreshUiStateDaysOfWeek()
        }


    }

    fun getAttributesOfSubject(itemIndex: Int): SubjectItem {
        return uiState.value!!.subjectItemList[itemIndex]
    }

    fun getAttributesOfSubjectWeekCalendar(dayOfWeekIndex: Int, itemIndex: Int): SubjectItem {
        return uiStateDaysOfWeek.value!!.subjectItemWeek[dayOfWeekIndex][itemIndex]
    }

    fun updateSubjectPosition(
       position: Int,
        subjectId: String
    ) {
        viewModelScope.launch {
            subjectRepository.updateSubjectPosition(position, subjectId)

        }
    }

    suspend fun getAmountOfQuestion(): Int {

        return subjectRepository.getAmountOfQuestion()

    }


    suspend fun fetchAllTopicsBySubject(subjectId: String): List<TopicItem> {


        return subjectRepository.fetchAllTopics(subjectId).map { topic ->
            TopicItem(
                id = topic.id,
                subjectId = topic.subjectId,
                title = topic.title,
                isCompleted = topic.isCompleted,
                date = topic.date,
                description = topic.description,
                performance = topic.performance,
                amountOfQuestions = topic.amountOfQuestions,
                match = topic.match,
                error = topic.error,
                position = topic.position
            )

        }

    }

    suspend fun getNumberOfQuestionsBySubject(subjectId: String): Int {
        return subjectRepository.getNumberOfQuestionsBySubject(subjectId)
    }

    suspend fun getNumberOfQuestionsOfDaysBySubject(subjectId: String, date: String): Int {
        return subjectRepository.getNumberOfQuestionsOfDaysBySubject(subjectId, date)
    }


    suspend fun getPerformanceBySubject(subjectId: String): Float {
        return subjectRepository.getPerformanceBySubject(subjectId)

    }

    suspend fun getPerformanceOfDay(subjectId: String, date: String): Float {
        return subjectRepository.getPerformanceOfDayBySubject(
            subjectId, date
        )
    }

    suspend fun getNumberTopicBySubject(subjectId: String): Int {
        return subjectRepository.countTopicBySubject(subjectId)
    }

    suspend fun getNumberTopicCompletedBySubject(subjectId: String): Int {
        return subjectRepository.countTopicCompletedBySubject(subjectId)
    }

    suspend fun getNumberTopicBySubjectByDate(subjectId: String, date: String): Int {
        return subjectRepository.countTopicBySubjectByDate(subjectId, date)
    }

    suspend fun getNumberTopicCompletedBySubjectByDate(subjectId: String, date: String): Int {
        return subjectRepository.countTopicCompletedBySubjectByDate(subjectId, date)
    }


    suspend fun updateToggle(isCompleted: Int, subjectId: String) {
        subjectRepository.updateSubjectToggle(isCompleted, subjectId)
    }


    suspend fun deleteSubject(id: String) {
        subjectRepository.delete(id)

    }

    suspend fun updateSubject(title: String, backgroundColor: Int, daysOfWeek: List<Int>,subjectId: String) {

        subjectRepository.updateSubject(title, backgroundColor, daysOfWeek, subjectId)
        refreshUiState()

    }

    suspend fun updateSubjectSelected(isSelected: Int, subjectId: String) {
        subjectRepository.updateSubjectIsSelected(isSelected, subjectId)
    }

    suspend fun refreshUiStateDaysOfWeek() {
        uiStateDaysOfWeek.postValue(UiStateWeek(getAllSubjectsDaysOfWeekUseCase()))
    }


    suspend fun refreshUiState() {
        uiState.postValue(UiState(getAllSubjectUseCase()))

    }
    suspend fun refreshWithNotCompletedItemsUiState() {
        uiState.postValue(UiState(subjectRepository.fetchOnlyCompletedSubjects().map { subjectDomain ->
            SubjectItem(

                id = subjectDomain.id,
                title = subjectDomain.title,
                isCompleted = subjectDomain.isCompleted,
                numbersOfTopics = 0,
                numbersOfTopicsCompleted = 0,
                backgroundColor = subjectDomain.backgroundColor,
                daysOfWeek = subjectDomain.daysOfWeek,
                isSelected = subjectDomain.isSelected,
                position = subjectDomain.position


            )

        }.sortedBy { it.position.inc() }))
    }
    suspend fun refreshWithNotCompletedUiStateDaysOfWeek() {
        uiStateDaysOfWeek.postValue(UiStateWeek(subjectRepository.getNotCompletedSubjectsDaysOfWeek().map { subjectL ->
            subjectL.map { subjectC ->
                SubjectItem(

                    id = subjectC.id,
                    title = subjectC.title,
                    isCompleted = subjectC.isCompleted,
                    numbersOfTopics = 0,
                    numbersOfTopicsCompleted = 0,
                    backgroundColor = subjectC.backgroundColor,
                    daysOfWeek = subjectC.daysOfWeek,
                    isSelected = subjectC.isSelected,
                    position = subjectC.position

                )


            }.sortedBy {
                it.position.inc()

            }
        }))
    }

    suspend fun refreshDatesState() {
        datesStateState.postValue(
            DatesState(calendarRepository.fetchDaysOfWeek(), calendarRepository.fetchDatesOfWeek())
        )

    }


}


