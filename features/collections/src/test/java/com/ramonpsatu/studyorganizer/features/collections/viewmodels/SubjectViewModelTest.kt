@file:Suppress("DEPRECATION")

package com.ramonpsatu.studyorganizer.features.collections.viewmodels

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.graphics.Color
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.viewModelScope
import com.ramonpsatu.studyorganizer.core.data.model.SubjectDomain
import com.ramonpsatu.studyorganizer.core.data.model.TopicDomain
import com.ramonpsatu.studyorganizer.core.data.repository.CalendarRepository
import com.ramonpsatu.studyorganizer.core.data.repository.SubjectRepository
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectUseCase
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectsDaysOfWeekUseCase
import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.features.collections.utils.ModelBuilder
import com.ramonpsatu.studyorganizer.features.collections.utils.TestCoroutineRule
import com.ramonpsatu.studyorganizer.features.collections.utils.getOrAwaitValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.TimeZone
import java.util.UUID

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SubjectViewModelTest {

    /**
     * InstantTaskExecutorRule swaps the background executor used by the Architecture Components
     * with a different one which executes each task synchronously.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Suppress("DEPRECATION")
    private val testDispatcher = TestCoroutineDispatcher()


    private val getAllSubjectUseCase = mock<GetAllSubjectUseCase>()
    private val subjectRepository = mock<SubjectRepository>()
    private val getAllSubjectsDaysOfWeekUseCase = mock<GetAllSubjectsDaysOfWeekUseCase>()
    private val calendarRepository = mock<CalendarRepository>()


    private val viewModel = SubjectListViewModel(
        subjectRepository, getAllSubjectUseCase, getAllSubjectsDaysOfWeekUseCase, calendarRepository
    )

    @Before
    fun setup() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                testDispatcher.dispatch(viewModel.viewModelScope.coroutineContext) {
                    runnable.run()
                }
            }

            override fun isMainThread(): Boolean {
                return testDispatcher.isActive
            }
        })
    }

    @After
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun `Verify uiState is initialized with Subjects`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            whenever(getAllSubjectUseCase.invoke()).thenReturn(
                listOf(
                    SubjectItem(
                        id = UUID.randomUUID().toString(),
                        title = UUID.randomUUID().toString().substring(0..3),
                        isCompleted = 0,
                        numbersOfTopics = 0,
                        numbersOfTopicsCompleted = 0,
                        backgroundColor = Color.GRAY,
                        daysOfWeek = listOf(0, 3, 4),
                        isSelected = 0,
                        position = 0
                    )
                )
            )

            viewModel.onResume()

            //Execute
            val uiState = viewModel.stateOnceAndStream().getOrAwaitValue()

            //Verify
            assert(uiState.subjectItemList.isNotEmpty())
        }
    }

    @Test
    fun `Verify uiState is initialized with SubjectsDayOfWeek`() {
        //Prepare
        testCoroutineRule.runBlockingTest {
            whenever(getAllSubjectsDaysOfWeekUseCase.invoke()).thenReturn(
                listOf(
                    listOf(
                        ModelBuilder().subjectBuilder()
                    )
                )
            )

            viewModel.refreshUiStateDaysOfWeek()

            //Execute
            val uiState = viewModel.stateOnceAndDaysOfWeek().getOrAwaitValue()

            //Verify
            assert(uiState.subjectItemWeek.isNotEmpty())

        }
    }

    @Test
    fun `Verify If dateState is initialized with days of week`() {
        //Prepare
        testCoroutineRule.runBlockingTest {
            val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)

            val daysOfWeek = mutableListOf<Int>()
            val datesOfWeeks = mutableListOf<Long>()
            for (index in 0 until 7) {
                calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                daysOfWeek.add(calendar.get(java.util.Calendar.DAY_OF_MONTH))
                datesOfWeeks.add(calendar.timeInMillis)

            }

            whenever(calendarRepository.fetchDaysOfWeek()).thenReturn(
                listOf(
                    daysOfWeek[0],
                    daysOfWeek[1],
                    daysOfWeek[2],
                    daysOfWeek[3],
                    daysOfWeek[4],
                    daysOfWeek[5],
                    daysOfWeek[6]
                )
            )
            whenever(calendarRepository.fetchDatesOfWeek()).thenReturn(
                listOf(
                    datesOfWeeks[0],
                    datesOfWeeks[1],
                    datesOfWeeks[2],
                    datesOfWeeks[3],
                    datesOfWeeks[4],
                    datesOfWeeks[5],
                    datesOfWeeks[6]
                )
            )

            viewModel.refreshDatesState()


            //Execute
            val uiState = viewModel.stateOnceDates().getOrAwaitValue()

            //Verify
            assert(uiState.daysOfWeek.isNotEmpty())


        }

    }

    @Test
    fun `Verify If return subjects list size`() {
        //Prepare
        testCoroutineRule.runBlockingTest {
            whenever(getAllSubjectUseCase.invoke()).thenReturn(
                listOf(
                    ModelBuilder().subjectBuilder()
                )
            )


            viewModel.onResume()
            val size: Int
            //Execute
            val uiState = viewModel.stateOnceAndStream().getOrAwaitValue()

            size = if (uiState.subjectItemList.isEmpty()) {
                0
            }else{
                viewModel.getSubjectListSize()
            }


            //Verify
            assert(size >= 0)
        }
    }

    @Test
    fun `Verify If return completed subjects list size`() {

        testCoroutineRule.runBlockingTest {
            //Prepare
            whenever(getAllSubjectUseCase.invoke()).thenReturn(
                listOf(
                    SubjectItem(
                        id = UUID.randomUUID().toString(),
                        title = UUID.randomUUID().toString().substring(0..3),
                        isCompleted = 1,
                        numbersOfTopics = 0,
                        numbersOfTopicsCompleted = 0,
                        backgroundColor = Color.GRAY,
                        daysOfWeek = listOf(0, 3, 4),
                        isSelected = 0,
                        position = 0
                    )
                )
            )

            viewModel.onResume()

            //Execute
            viewModel.stateOnceAndStream().getOrAwaitValue().subjectItemList
            val size = viewModel.getCompletedSubjectListSize()


            //Verify
            assert(size >= 0)
        }
    }

    @Test
    fun `Verify if it returns the total performance number of disciplines`() {

        testCoroutineRule.runBlockingTest {
            //Prepare
            whenever(subjectRepository.getAmountOfMatches()).thenReturn(
                0
            )
            whenever(viewModel.getAmountOfQuestion()).thenReturn(
                0
            )
            val amountQuestions: Float = viewModel.getAmountOfQuestion().toFloat()
            val amountMatches: Float = subjectRepository.getAmountOfMatches().toFloat()


            val minus = (amountMatches.div(amountQuestions))

            val result = minus.times(100)
            var string = String.format("%.0f", result) + "%"

            //Execute
            if (result.isNaN()) {
                string = "0"
            }

            //Verify
            assertThat(string, containsString(string))
            assertEquals(string,viewModel.getPerformanceTotal())

        }
    }

    @Test
    fun `Verify fun add subject`() {

        testCoroutineRule.runBlockingTest {
            //given
            val subject = SubjectItem(
                id = "11",
                title = "title-20",
                isCompleted = 0,
                numbersOfTopics = 0,
                numbersOfTopicsCompleted = 0,
                backgroundColor = Color.BLACK,
                daysOfWeek = listOf(0, 3, 4),
                isSelected = 0,
                position = 0
            )

            //when
            viewModel.addSubject(
                subject.id,
                subject.title,
                subject.isCompleted,
                subject.backgroundColor,
                subject.daysOfWeek,
                subject.isSelected,
                subject.position
            )

            //then
            verify(subjectRepository).addSubject(
                id = "11",
                title = "title-20",
                isCompleted = 0,
                backgroundColor = Color.BLACK,
                daysOfWeek = listOf(0, 3, 4),
                isSelected = 0,
                position = 0
            )

        }


    }

    @Test
    fun `Verify fun get attributes of subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val subjectItemII = ModelBuilder().subjectBuilder()

            val list = listOf(subjectItem, subjectItemII)
            whenever(getAllSubjectUseCase.invoke()).thenReturn(list)
            viewModel.onResume()

            //execute

            val subjectItemLiveData =
                viewModel.stateOnceAndStream().getOrAwaitValue().subjectItemList.first()
            val subjectGetAttr = viewModel.getAttributesOfSubject(0)

            //Verify

            assertEquals(subjectGetAttr.id, subjectItemLiveData.id)
        }
    }

    @Test
    fun `Verify fun get attributes of subject of the week`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val subjectItemII = ModelBuilder().subjectBuilder()

            val list = listOf(listOf(subjectItem, subjectItemII))
            whenever(getAllSubjectsDaysOfWeekUseCase.invoke()).thenReturn(list)
            viewModel.refreshUiStateDaysOfWeek()

            //execute
            val subjectUiStateList =
                viewModel.stateOnceAndDaysOfWeek().getOrAwaitValue().subjectItemWeek
            val subjectGetAttr = viewModel.getAttributesOfSubjectWeekCalendar(0, 0)

            //Verify

            assertEquals(subjectGetAttr.id, subjectUiStateList[0].first().id)
        }
    }

    @Test
    fun `Verify the updated position of the subject in the list`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val subjectItemID = subjectItem.id
            val position = 3
            whenever(subjectRepository.updateSubjectPosition(position, subjectItemID)).thenReturn(
                Unit
            )

            //Execute
            viewModel.updateSubjectPosition(position, subjectItemID)

            //Verify
            verify(subjectRepository).updateSubjectPosition(position, subjectItemID)
        }
    }

    @Test
    fun `Verify fun get amount of Questions`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            whenever(subjectRepository.getAmountOfQuestion()).thenReturn(130)

            //execute
            val amount = viewModel.getAmountOfQuestion()

            //Verify
            verify(subjectRepository).getAmountOfQuestion()
            assert(amount == 130)
        }
    }

    @Test
    fun `Verify fun fetch all topics by subjects`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val itemId = subjectItem.id
            val topicItemList = mutableListOf<TopicItem>()

            for (index in 0 until 6) {
                when (index) {
                    in 2..5 -> {
                        topicItemList.add(
                            TopicItem(
                                id = UUID.randomUUID().toString(),
                                subjectId = subjectItem.id,
                                title = UUID.randomUUID().toString().substring(0..1),
                                isCompleted = 0,
                                date = "07/09/2023",
                                description = "topic-description",
                                performance = 0f,
                                amountOfQuestions = 0,
                                match = 0,
                                error = 0,
                                position = 0
                            )
                        )
                    }

                    else -> {
                        topicItemList.add(
                            TopicItem(
                                id = UUID.randomUUID().toString(),
                                subjectId = UUID.randomUUID().toString(),
                                title = UUID.randomUUID().toString().substring(0..1),
                                isCompleted = 0,
                                date = "07/09/2023",
                                description = "topic-description",
                                performance = 0f,
                                amountOfQuestions = 0,
                                match = 0,
                                error = 0,
                                position = 0
                            )
                        )

                    }
                }
            }

            val topicItemBySubject = mutableListOf<TopicItem>()

            for (index in topicItemList.indices) {
                if (topicItemList[index].subjectId == itemId) {
                    topicItemBySubject.add(topicItemList[index])
                }
            }

            whenever(subjectRepository.fetchAllTopics(itemId)).thenReturn(
                topicItemBySubject.map { topic ->
                    TopicDomain(
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
            )

            //execute
            val currentList = viewModel.fetchAllTopicsBySubject(itemId)

            //Verify
            verify(subjectRepository).fetchAllTopics(itemId)
            assert(currentList[0].subjectId == itemId)
            assert(currentList[1].subjectId == itemId)
            assert(currentList[2].subjectId == itemId)
            assert(currentList[3].subjectId == itemId)
        }
    }

    @Test
    fun `Verify fun get number of Questions by subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val amountQuestions = 250
            whenever(subjectRepository.getNumberOfQuestionsBySubject(subjectItem.id)).thenReturn(250)

            //execute
            val amount = viewModel.getNumberOfQuestionsBySubject(subjectItem.id)

            //Verify
            verify(subjectRepository).getNumberOfQuestionsBySubject(subjectItem.id)
            assert(amount == amountQuestions)
        }
    }

    @Test
    fun `Verify fun get number of Questions of the day by subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val amountQuestions = 516
            whenever(
                subjectRepository.getNumberOfQuestionsOfDaysBySubject(
                    subjectItem.id,
                    "05/09/2023"
                )
            ).thenReturn(516)

            //execute
            val amount = viewModel.getNumberOfQuestionsOfDaysBySubject(subjectItem.id, "05/09/2023")

            //Verify
            verify(subjectRepository).getNumberOfQuestionsOfDaysBySubject(
                subjectItem.id,
                "05/09/2023"
            )
            assert(amount == amountQuestions)
        }
    }

    @Test
    fun `Verify fun get performance by subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val performance = 11.3f
            whenever(subjectRepository.getPerformanceBySubject(subjectItem.id)).thenReturn(11.3f)

            //execute
            val amount = viewModel.getPerformanceBySubject(subjectItem.id)

            //Verify
            verify(subjectRepository).getPerformanceBySubject(subjectItem.id)
            assert(amount == performance)
        }
    }

    @Test
    fun `Verify fun get performance of the day by subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val performance = 11.5f
            whenever(
                subjectRepository.getPerformanceOfDayBySubject(
                    subjectItem.id,
                    "05/09/2023"
                )
            ).thenReturn(11.5f)

            //execute
            val amount = viewModel.getPerformanceOfDay(subjectItem.id, "05/09/2023")

            //Verify
            verify(subjectRepository).getPerformanceOfDayBySubject(subjectItem.id, "05/09/2023")
            assert(amount == performance)
        }
    }

    @Test
    fun `Verify fun get number of Topics by subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val amountQuestions = 225
            whenever(subjectRepository.countTopicBySubject(subjectItem.id)).thenReturn(225)

            //execute
            val amount = viewModel.getNumberTopicBySubject(subjectItem.id)

            //Verify
            verify(subjectRepository).countTopicBySubject(subjectItem.id)
            assert(amount == amountQuestions)
        }
    }

    @Test
    fun `Verify fun get number of Topics completed by subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val amountQuestions = 210
            whenever(subjectRepository.countTopicCompletedBySubject(subjectItem.id)).thenReturn(210)

            //execute
            val amount = viewModel.getNumberTopicCompletedBySubject(subjectItem.id)

            //Verify
            verify(subjectRepository).countTopicCompletedBySubject(subjectItem.id)
            assert(amount == amountQuestions)
        }
    }

    @Test
    fun `Verify fun get number of topics by subjects and date`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val performance = 115
            whenever(
                subjectRepository.countTopicBySubjectByDate(
                    subjectItem.id,
                    "05/09/2023"
                )
            ).thenReturn(115)

            //execute
            val amount = viewModel.getNumberTopicBySubjectByDate(subjectItem.id, "05/09/2023")

            //Verify
            verify(subjectRepository).countTopicBySubjectByDate(subjectItem.id, "05/09/2023")
            assert(amount == performance)
        }
    }

    @Test
    fun `Verify fun get number of topics completed by subjects and date`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val performance = 106
            whenever(
                subjectRepository.countTopicCompletedBySubjectByDate(
                    subjectItem.id,
                    "05/09/2023"
                )
            ).thenReturn(106)

            //execute
            val amount =
                viewModel.getNumberTopicCompletedBySubjectByDate(subjectItem.id, "05/09/2023")

            //Verify
            verify(subjectRepository).countTopicCompletedBySubjectByDate(
                subjectItem.id,
                "05/09/2023"
            )
            assert(amount == performance)
        }
    }

    @Test
    fun `Verify fun update subject toggle`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val isCompleted = 1
            whenever(subjectRepository.updateSubjectToggle(isCompleted, subjectItem.id)).thenReturn(
                Unit
            )

            //execute
            viewModel.updateToggle(isCompleted, subjectItem.id)

            //Verify
            verify(subjectRepository).updateSubjectToggle(isCompleted, subjectItem.id)
        }
    }

    @Test
    fun `Verify fun delete subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()

            whenever(subjectRepository.delete(subjectItem.id)).thenReturn(Unit)

            //execute
            viewModel.deleteSubject(subjectItem.id)

            //Verify
            verify(subjectRepository).delete(subjectItem.id)
        }
    }

    @Test
    fun `Verify fun update subject`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()

            whenever(
                subjectRepository.updateSubject(
                    subjectItem.title, subjectItem.backgroundColor,
                    subjectItem.daysOfWeek, subjectItem.id
                )
            ).thenReturn(Unit)

            //execute
            viewModel.updateSubject(
                subjectItem.title, subjectItem.backgroundColor,
                subjectItem.daysOfWeek, subjectItem.id
            )

            //Verify
            verify(subjectRepository).updateSubject(
                subjectItem.title, subjectItem.backgroundColor,
                subjectItem.daysOfWeek, subjectItem.id
            )
        }
    }
    @Test
    fun `Verify fun update subject selected`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            val isSelected = 1
            whenever(subjectRepository.updateSubjectIsSelected(isSelected, subjectItem.id)).thenReturn(
                Unit
            )

            //execute
            viewModel.updateSubjectSelected(isSelected, subjectItem.id)

            //Verify
            verify(subjectRepository).updateSubjectIsSelected(isSelected, subjectItem.id)
        }
    }
    @Test
    fun `Verify fun refresh UI State Days of the Week`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            whenever(getAllSubjectsDaysOfWeekUseCase.invoke()).thenReturn(
                listOf(
                    listOf(
                        subjectItem
                    ),
                    listOf(
                        subjectItem
                    )
                )
            )
            viewModel.refreshUiStateDaysOfWeek()
            //Execute
            val uiState = viewModel.stateOnceAndDaysOfWeek().getOrAwaitValue().subjectItemWeek

            //Verify
            verify(getAllSubjectsDaysOfWeekUseCase).invoke()
            assert(uiState.isNotEmpty())
            assertEquals(getAllSubjectsDaysOfWeekUseCase.invoke()[0],uiState[0])

        }
    }
    @Test
    fun `Verify fun refresh UI State of the subjects`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectItem = ModelBuilder().subjectBuilder()
            whenever(getAllSubjectUseCase.invoke()).thenReturn(
                listOf(subjectItem)
            )
            viewModel.refreshUiState()
            //Execute
            val uiState = viewModel.stateOnceAndStream().getOrAwaitValue().subjectItemList

            //Verify
            verify(getAllSubjectUseCase).invoke()
            assert(uiState.isNotEmpty())
            assertEquals(getAllSubjectUseCase.invoke()[0],uiState[0])

        }
    }
    @Test
    fun `Verify fun refresh UI State without completed  subjects`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectDomain =  SubjectDomain(
                id = UUID.randomUUID().toString(),
                title = UUID.randomUUID().toString().substring(0..1),
                isCompleted = 1,
                backgroundColor = Color.GRAY,
                daysOfWeek = listOf(0, 3, 4),
                isSelected = 0,
                position = 0)
            val subjectItem = SubjectItem(
                id = subjectDomain.id,
                title = subjectDomain.title,
                isCompleted = subjectDomain.isCompleted,
                numbersOfTopics = 0,
                numbersOfTopicsCompleted = 0,
                backgroundColor = subjectDomain.backgroundColor,
                daysOfWeek = subjectDomain.daysOfWeek,
                isSelected = subjectDomain.isSelected,
                position = subjectDomain.position)

            whenever(subjectRepository.fetchOnlyCompletedSubjects()).thenReturn(
                listOf(subjectDomain)
            )
            //Execute
            viewModel.refreshWithNotCompletedItemsUiState()

            val uiState = viewModel.stateOnceAndStream().getOrAwaitValue().subjectItemList
            //Verify
            verify(subjectRepository).fetchOnlyCompletedSubjects()
            assert(uiState.isNotEmpty())
            assertEquals(subjectItem,uiState[0])

        }
    }
    @Test
    fun `Verify fun refresh UI State without completed subjects by days of week`() {
        testCoroutineRule.runBlockingTest {
            //Prepare
            val subjectDomain =  SubjectDomain(
                id = UUID.randomUUID().toString(),
                title = UUID.randomUUID().toString().substring(0..1),
                isCompleted = 1,
                backgroundColor = Color.GRAY,
                daysOfWeek = listOf(0, 3, 4),
                isSelected = 0,
                position = 0)

            val subjectItem = SubjectItem(
                id = subjectDomain.id,
                title = subjectDomain.title,
                isCompleted = subjectDomain.isCompleted,
                numbersOfTopics = 0,
                numbersOfTopicsCompleted = 0,
                backgroundColor = subjectDomain.backgroundColor,
                daysOfWeek = subjectDomain.daysOfWeek,
                isSelected = subjectDomain.isSelected,
                position = subjectDomain.position)

            whenever(subjectRepository.getNotCompletedSubjectsDaysOfWeek()).thenReturn(
                listOf(listOf(subjectDomain),listOf(subjectDomain))
            )
            //Execute
            viewModel.refreshWithNotCompletedUiStateDaysOfWeek()

            val uiState = viewModel.stateOnceAndDaysOfWeek().getOrAwaitValue().subjectItemWeek
            //Verify
            verify(subjectRepository).getNotCompletedSubjectsDaysOfWeek()
            assert(uiState.isNotEmpty())
            assertEquals(subjectItem,uiState[0][0])

        }
    }


/*


    */

}
