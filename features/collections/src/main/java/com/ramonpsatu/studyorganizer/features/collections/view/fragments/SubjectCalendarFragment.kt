package com.ramonpsatu.studyorganizer.features.collections.view.fragments


import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.utils.SubjectLifecycleObserver
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.TopicViewModel
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.FridayListAdapter
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.MondayListAdapter
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.SaturdayListAdapter
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.SundayListAdapter
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.ThursdayListAdapter
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.TuesdayListAdapter
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.WednesdayListAdapter
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentSubjectCalendarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


/**
 * A [Fragment] that displays a list of subjects by days of week.
 */
@AndroidEntryPoint
class SubjectCalendarFragment : Fragment(), View.OnClickListener {


    private var _binding: FragmentSubjectCalendarBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapterM: MondayListAdapter
    private lateinit var adapterT: TuesdayListAdapter
    private lateinit var adapterW: WednesdayListAdapter
    private lateinit var adapterTh: ThursdayListAdapter
    private lateinit var adapterF: FridayListAdapter
    private lateinit var adapterS: SaturdayListAdapter
    private lateinit var adapterSun: SundayListAdapter

    private var mondayListSize = 0
    private var tuesdayListSize = 0
    private var wednesdayListSize = 0
    private var thursdayListSize = 0
    private var fridayListSize = 0
    private var saturdayListSize = 0
    private var sundayListSize = 0

    private lateinit var subjectListViewModel: SubjectListViewModel


    private lateinit var topicViewModel: TopicViewModel
    private lateinit var preferenceViewModel: SharedPreferencesViewModel
    private var removeComplete = false

    private lateinit var datesOfWeekList: List<Long>
    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var listOfViewTextNumber: List<TextView>
    private lateinit var listOfViewTextLetter: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subjectListViewModel = ViewModelProvider(this)[SubjectListViewModel::class.java]
        topicViewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        preferenceViewModel = ViewModelProvider(this)[SharedPreferencesViewModel::class.java]

        lifecycle.addObserver(SubjectLifecycleObserver(subjectListViewModel))





        scope.launch {
            subjectListViewModel.refreshDatesState()
        }

        adapterM = MondayListAdapter({ viewId ->
            scope.launch {
                StateHolderObject.setAttributesSubjectItem(

                    subjectListViewModel.getAttributesOfSubjectWeekCalendar(
                        0,
                        viewId
                    )
                )

                setDateInMySafeArgs(0, 2)

                topicViewModel.refreshUiStateOfTopic(
                    StateHolderObject.safeArgsSubjectItem().id,
                    StateHolderObject.dateTodayForTopic
                )

            }

            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectCalendarFragment_to_topicFragment)

        }, subjectListViewModel)

        adapterT = TuesdayListAdapter({ viewId ->
            scope.launch {
                StateHolderObject.setAttributesSubjectItem(
                    subjectListViewModel.getAttributesOfSubjectWeekCalendar(
                        1,
                        viewId
                    )
                )


                setDateInMySafeArgs(1, 3)

                topicViewModel.refreshUiStateOfTopic(
                    StateHolderObject.safeArgsSubjectItem().id,
                    StateHolderObject.dateTodayForTopic
                )


            }
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectCalendarFragment_to_topicFragment)


        }, subjectListViewModel)
        adapterW = WednesdayListAdapter({ viewId ->
            scope.launch {
                StateHolderObject.setAttributesSubjectItem(
                    subjectListViewModel.getAttributesOfSubjectWeekCalendar(
                        2,
                        viewId
                    )
                )


                setDateInMySafeArgs(2, 4)

                topicViewModel.refreshUiStateOfTopic(
                    StateHolderObject.safeArgsSubjectItem().id,
                    StateHolderObject.dateTodayForTopic
                )

            }
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectCalendarFragment_to_topicFragment)

        }, subjectListViewModel)

        adapterTh = ThursdayListAdapter({ viewId ->
            scope.launch {
                StateHolderObject.setAttributesSubjectItem(
                    subjectListViewModel.getAttributesOfSubjectWeekCalendar(
                        3,
                        viewId
                    )
                )

                setDateInMySafeArgs(3, 5)

                topicViewModel.refreshUiStateOfTopic(
                    StateHolderObject.safeArgsSubjectItem().id,
                    StateHolderObject.dateTodayForTopic
                )


            }
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectCalendarFragment_to_topicFragment)

        }, subjectListViewModel)
        adapterF = FridayListAdapter({ viewId ->
            scope.launch {
                StateHolderObject.setAttributesSubjectItem(
                    subjectListViewModel.getAttributesOfSubjectWeekCalendar(
                        4,
                        viewId
                    )
                )

                setDateInMySafeArgs(4, 6)

                topicViewModel.refreshUiStateOfTopic(
                    StateHolderObject.safeArgsSubjectItem().id,
                    StateHolderObject.dateTodayForTopic
                )

            }
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectCalendarFragment_to_topicFragment)

        }, subjectListViewModel)

        adapterS = SaturdayListAdapter({ viewId ->
            scope.launch {
                StateHolderObject.setAttributesSubjectItem(
                    subjectListViewModel.getAttributesOfSubjectWeekCalendar(
                        5,
                        viewId
                    )
                )


                setDateInMySafeArgs(5, 7)

                topicViewModel.refreshUiStateOfTopic(
                    StateHolderObject.safeArgsSubjectItem().id,
                    StateHolderObject.dateTodayForTopic
                )

            }
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectCalendarFragment_to_topicFragment)

        }, subjectListViewModel)
        adapterSun = SundayListAdapter({ viewId ->
            scope.launch {
                StateHolderObject.setAttributesSubjectItem(
                    subjectListViewModel.getAttributesOfSubjectWeekCalendar(
                        6,
                        viewId
                    )
                )

                setDateInMySafeArgs(6, 1)

                topicViewModel.refreshUiStateOfTopic(
                    StateHolderObject.safeArgsSubjectItem().id,
                    StateHolderObject.dateTodayForTopic
                )

            }
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectCalendarFragment_to_topicFragment)

        }, subjectListViewModel)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubjectCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdaptersAndRecycleViews()
        starLists()
        setListeners()

        preferenceViewModel.viewModelScope.launch {
            removeComplete =preferenceViewModel.getRemoveCompleted(requireContext())
        }

        subjectListViewModel.viewModelScope.launch {

            if (removeComplete){
                subjectListViewModel.refreshWithNotCompletedUiStateDaysOfWeek()
            }else{
                subjectListViewModel.refreshUiStateDaysOfWeek()
            }
        }


        subjectListViewModel.stateOnceDates().observe(viewLifecycleOwner) {

            bindOneWeekCalendarFeature(it)

        }

        subjectListViewModel.stateOnceAndDaysOfWeek().observe(viewLifecycleOwner) {

            bindUiStateDaysOfWeek(it)
        }

        handlerInformativeGuide()

    }


    override fun onClick(v: View) {

        switchDayInCalendarUI(v)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handlerInformativeGuide(){
        preferenceViewModel.viewModelScope.launch {

            if (!preferenceViewModel.getStateInformativeGuideUI(requireContext())){
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_calendar))
                    setMessage(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_calendar_screen))
                    create()
                }.show()
            }
        }
    }

    private fun setDateInMySafeArgs(indexDay: Int, dayOfWeek: Int) {

        val date = DateFormat.format("yyyyMMdd", datesOfWeekList[indexDay]).toString()

        StateHolderObject.dateTodayForTopic = date
        StateHolderObject.dayOfWeek = dayOfWeek
    }


    /**
     * Bind UI State to View.
     *
     * Update list of subjects according to updates.
     */
    private fun bindUiStateDaysOfWeek(uiState: SubjectListViewModel.UiStateWeek) {

        adapterM.updateSubjectMonday(uiState.subjectItemWeek[0])
        adapterT.updateSubjectTuesday(uiState.subjectItemWeek[1])
        adapterW.updateSubjectWednesday(uiState.subjectItemWeek[2])
        adapterTh.updateSubjectThursday(uiState.subjectItemWeek[3])
        adapterF.updateSubjectFriday(uiState.subjectItemWeek[4])
        adapterS.updateSubjectSaturday(uiState.subjectItemWeek[5])
        adapterSun.updateSubjectSunday(uiState.subjectItemWeek[6])


        mondayListSize = uiState.subjectItemWeek[0].size
        tuesdayListSize = uiState.subjectItemWeek[1].size
        wednesdayListSize = uiState.subjectItemWeek[2].size
        thursdayListSize = uiState.subjectItemWeek[3].size
        fridayListSize = uiState.subjectItemWeek[4].size
        saturdayListSize = uiState.subjectItemWeek[5].size
        sundayListSize = uiState.subjectItemWeek[6].size

        setListOfDay()

    }


    private fun bindOneWeekCalendarFeature(uiState: SubjectListViewModel.DatesState) {

        datesOfWeekList = uiState.datesOfWeek

        listOfViewTextNumber[0].text = uiState.daysOfWeek[0].toString()
        listOfViewTextNumber[1].text = uiState.daysOfWeek[1].toString()
        listOfViewTextNumber[2].text = uiState.daysOfWeek[2].toString()
        listOfViewTextNumber[3].text = uiState.daysOfWeek[3].toString()
        listOfViewTextNumber[4].text = uiState.daysOfWeek[4].toString()
        listOfViewTextNumber[5].text = uiState.daysOfWeek[5].toString()
        listOfViewTextNumber[6].text = uiState.daysOfWeek[6].toString()

        setListOfDay()

    }

    private fun formatDate(indexDay: Int) {

        val date = DateFormat.format("yyyyMMdd", datesOfWeekList[indexDay]).toString()
        val year = date.substring(0..3).toInt()
        val month = date.substring(4..5).toInt() - 1
        val dayOfMonth = date.substring(6 until date.length).toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        binding.textViewDateOnCalendar.text = MyCalendar.formatDateByOneView(
            calendar.time,
            calendar.get(Calendar.DAY_OF_WEEK), requireContext()
        )
    }

    /**
     * [setListOfDay] loads the current
     * day's calendar and date in the header.
     */
    private fun setListOfDay() {
        when (MyCalendar.dayOfWeek) {

            1 -> {

                setTextInformation(sundayListSize)

                setAdaptersInvisibleOrVisible(binding.recycleViewCalendarSunday)
                setColorAndBackgroundTexts(
                    binding.textViewSundayLetter,
                    binding.textViewSundayNumberLetterTouchable
                )

                formatDate(6)

                MyCalendar.day = 6
                binding.textViewSubjectListSize.text = sundayListSize.toString()
            }

            2 -> {

                setTextInformation(mondayListSize)

                setAdaptersInvisibleOrVisible(binding.recycleViewCalendarMonday)
                setColorAndBackgroundTexts(
                    binding.textViewMondayLetter,
                    binding.textViewMondayNumberLetterTouchable
                )

                formatDate(0)
                MyCalendar.day = 0

                binding.textViewSubjectListSize.text = mondayListSize.toString()


            }

            3 -> {

                setTextInformation(tuesdayListSize)
                setAdaptersInvisibleOrVisible(binding.recycleViewCalendarTuesday)
                setColorAndBackgroundTexts(
                    binding.textViewTuesdayLetter,
                    binding.textViewTuesdayNumberLetterTouchable
                )
                formatDate(1)
                MyCalendar.day = 1
                binding.textViewSubjectListSize.text = tuesdayListSize.toString()
            }

            4 -> {


                setTextInformation(wednesdayListSize)

                setAdaptersInvisibleOrVisible(binding.recycleViewCalendarWednesday)
                setColorAndBackgroundTexts(
                    binding.textViewWednesdayLetter,
                    binding.textViewWednesdayNumberLetterTouchable
                )

                formatDate(2)
                MyCalendar.day = 2
                binding.textViewSubjectListSize.text = wednesdayListSize.toString()

            }

            5 -> {

                setTextInformation(thursdayListSize)

                setAdaptersInvisibleOrVisible(binding.recycleViewCalendarThursday)
                setColorAndBackgroundTexts(
                    binding.textViewThursdayLetter,
                    binding.textViewThursdayNumberLetterTouchable
                )

                formatDate(3)
                MyCalendar.day = 3
                binding.textViewSubjectListSize.text = thursdayListSize.toString()
            }

            6 -> {

                setTextInformation(fridayListSize)
                setAdaptersInvisibleOrVisible(binding.recycleViewCalendarFriday)
                setColorAndBackgroundTexts(
                    binding.textViewFridayLetter,
                    binding.textViewFridayNumberLetterTouchable
                )
                formatDate(4)
                MyCalendar.day = 4
                binding.textViewSubjectListSize.text = fridayListSize.toString()
            }

            7 -> {

                setTextInformation(saturdayListSize)
                setAdaptersInvisibleOrVisible(binding.recycleViewCalendarSaturday)
                setColorAndBackgroundTexts(
                    binding.textViewSaturdayLetter,
                    binding.textViewSaturdayNumberLetterTouchable
                )

                formatDate(5)
                MyCalendar.day = 5
                binding.textViewSubjectListSize.text = saturdayListSize.toString()
            }


        }


    }


    private fun switchDayInCalendarUI(v: View): View {

        scope.launch(Dispatchers.Main) {

            when (v.id) {
                //Monday
                binding.textViewMondayNumberLetterTouchable.id -> {

                    setTextInformation(mondayListSize)



                    setAdaptersInvisibleOrVisible(binding.recycleViewCalendarMonday)
                    setColorAndBackgroundTexts(
                        binding.textViewMondayLetter,
                        binding.textViewMondayNumberLetterTouchable
                    )

                    formatDate(0)




                    MyCalendar.day = 0
                    binding.textViewSubjectListSize.text = mondayListSize.toString()
                }
                //Tuesday
                binding.textViewTuesdayNumberLetterTouchable.id -> {
                    setTextInformation(tuesdayListSize)
                    setAdaptersInvisibleOrVisible(binding.recycleViewCalendarTuesday)
                    setColorAndBackgroundTexts(
                        binding.textViewTuesdayLetter,
                        binding.textViewTuesdayNumberLetterTouchable
                    )


                    formatDate(1)


                    MyCalendar.day = 1
                    binding.textViewSubjectListSize.text = tuesdayListSize.toString()

                }
                //Wednesday
                binding.textViewWednesdayNumberLetterTouchable.id -> {
                    setTextInformation(wednesdayListSize)

                    setAdaptersInvisibleOrVisible(binding.recycleViewCalendarWednesday)
                    setColorAndBackgroundTexts(
                        binding.textViewWednesdayLetter,
                        binding.textViewWednesdayNumberLetterTouchable
                    )

                    formatDate(2)

                    MyCalendar.day = 2
                    binding.textViewSubjectListSize.text = wednesdayListSize.toString()
                }
                //Thursday
                binding.textViewThursdayNumberLetterTouchable.id -> {
                    setTextInformation(thursdayListSize)
                    setAdaptersInvisibleOrVisible(binding.recycleViewCalendarThursday)
                    setColorAndBackgroundTexts(
                        binding.textViewThursdayLetter,
                        binding.textViewThursdayNumberLetterTouchable
                    )

                    formatDate(3)

                    MyCalendar.day = 3
                    binding.textViewSubjectListSize.text = thursdayListSize.toString()
                }
                //Friday
                binding.textViewFridayNumberLetterTouchable.id -> {
                    setTextInformation(fridayListSize)
                    setAdaptersInvisibleOrVisible(binding.recycleViewCalendarFriday)
                    setColorAndBackgroundTexts(
                        binding.textViewFridayLetter,
                        binding.textViewFridayNumberLetterTouchable
                    )

                    formatDate(4)


                    MyCalendar.day = 4
                    binding.textViewSubjectListSize.text = fridayListSize.toString()
                }
                //Saturday
                binding.textViewSaturdayNumberLetterTouchable.id -> {
                    setTextInformation(saturdayListSize)
                    setAdaptersInvisibleOrVisible(binding.recycleViewCalendarSaturday)
                    setColorAndBackgroundTexts(
                        binding.textViewSaturdayLetter,
                        binding.textViewSaturdayNumberLetterTouchable
                    )

                    formatDate(5)

                    MyCalendar.day = 5
                    binding.textViewSubjectListSize.text = saturdayListSize.toString()
                }
                //Sunday
                binding.textViewSundayNumberLetterTouchable.id -> {
                    setTextInformation(sundayListSize)
                    setAdaptersInvisibleOrVisible(binding.recycleViewCalendarSunday)
                    setColorAndBackgroundTexts(
                        binding.textViewSundayLetter,
                        binding.textViewSundayNumberLetterTouchable
                    )

                    formatDate(6)

                    MyCalendar.day = 6
                    binding.textViewSubjectListSize.text = sundayListSize.toString()
                }


            }
        }


        return v
    }


    private fun setTextInformation(listSize: Int) {
        if (listSize == 0) {
            binding.textViewExhibitionInfor.visibility = View.VISIBLE
        } else {
            binding.textViewExhibitionInfor.visibility = View.INVISIBLE
        }
    }


    private fun setAdaptersInvisibleOrVisible(vG: ViewGroup) {
        val adapterList = mutableListOf(
            binding.recycleViewCalendarMonday,
            binding.recycleViewCalendarTuesday,
            binding.recycleViewCalendarWednesday,
            binding.recycleViewCalendarThursday,
            binding.recycleViewCalendarFriday,
            binding.recycleViewCalendarSaturday,
            binding.recycleViewCalendarSunday
        )

        val indexVisible = adapterList.indexOfFirst { recyclerView ->
            recyclerView.id == vG.id

        }


        for (vGroup in adapterList) {

            if (vG.id == vGroup.id) {

                adapterList[indexVisible].visibility = ViewGroup.VISIBLE

            } else if (vGroup.id != adapterList[indexVisible].id) {

                vGroup.visibility = ViewGroup.INVISIBLE

            }

        }

    }


    private fun setTextColorWhite(letter: TextView) {

        letter.setTextColor(Color.WHITE)


    }

    private fun setColorAndBackgroundTexts(
        viewBindingLetter: TextView,
        viewBindingNumber: TextView
    ) {

        listOfViewTextLetter.forEach { view ->

            if (view.id == viewBindingLetter.id) {
                viewBindingLetter.setBackgroundResource(com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_background_texts_fields)
                viewBindingLetter.background.setTint(
                    getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.black_minus_2
                    )
                )
                setTextColorWhite(viewBindingLetter)


            } else {
                view.setTextColor(
                    getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.black_minus_2
                    )
                )
                view.setBackgroundColor(
                    getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_textViews_inCalendar_screen
                    )
                )
            }

        }

        listOfViewTextNumber.forEach { view ->

            if (view.id == viewBindingNumber.id) {
                setTextColorWhite(viewBindingNumber)

            } else {
                view.setTextColor(
                    getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.black
                    )
                )
            }

        }


    }

    private fun starLists() {
        listOfViewTextNumber = listOf(
            binding.textViewMondayNumberLetterTouchable,
            binding.textViewTuesdayNumberLetterTouchable,
            binding.textViewWednesdayNumberLetterTouchable,
            binding.textViewThursdayNumberLetterTouchable,
            binding.textViewFridayNumberLetterTouchable,
            binding.textViewSaturdayNumberLetterTouchable,
            binding.textViewSundayNumberLetterTouchable
        )
        listOfViewTextLetter = listOf(
            binding.textViewMondayLetter,
            binding.textViewTuesdayLetter,
            binding.textViewWednesdayLetter,
            binding.textViewThursdayLetter,
            binding.textViewFridayLetter,
            binding.textViewSaturdayLetter,
            binding.textViewSundayLetter
        )
    }

    private fun setAdaptersAndRecycleViews() {
        binding.recycleViewCalendarMonday.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recycleViewCalendarMonday.adapter = adapterM

        binding.recycleViewCalendarTuesday.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recycleViewCalendarTuesday.adapter = adapterT

        binding.recycleViewCalendarWednesday.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recycleViewCalendarWednesday.adapter = adapterW

        binding.recycleViewCalendarThursday.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recycleViewCalendarThursday.adapter = adapterTh

        binding.recycleViewCalendarFriday.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recycleViewCalendarFriday.adapter = adapterF

        binding.recycleViewCalendarSaturday.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recycleViewCalendarSaturday.adapter = adapterS

        binding.recycleViewCalendarSunday.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recycleViewCalendarSunday.adapter = adapterSun

    }

    private fun setListeners() {

        binding.textViewMondayNumberLetterTouchable.setOnClickListener(this)
        binding.textViewTuesdayNumberLetterTouchable.setOnClickListener(this)
        binding.textViewWednesdayNumberLetterTouchable.setOnClickListener(this)
        binding.textViewThursdayNumberLetterTouchable.setOnClickListener(this)
        binding.textViewFridayNumberLetterTouchable.setOnClickListener(this)
        binding.textViewSaturdayNumberLetterTouchable.setOnClickListener(this)
        binding.textViewSundayNumberLetterTouchable.setOnClickListener(this)

    }


}