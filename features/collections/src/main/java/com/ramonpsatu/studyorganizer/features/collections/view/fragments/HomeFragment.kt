package com.ramonpsatu.studyorganizer.features.collections.view.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.core.data.database.entity.Revision
import com.ramonpsatu.studyorganizer.core.data.utils.hideVirtualKeyboard
import com.ramonpsatu.studyorganizer.core.data.utils.toastMessageLong
import com.ramonpsatu.studyorganizer.features.collections.listeners.ToggleClickListener
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.RevisionListAdapter
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.RevisionViewModel
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.TopicViewModel
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class HomeFragment : Fragment(), DatePicker.OnDateChangedListener,
    DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var subjectListViewModel: SubjectListViewModel
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var preferenceViewModel: SharedPreferencesViewModel
    private lateinit var adapter: RevisionListAdapter
    private lateinit var revisionViewModel: RevisionViewModel

    private var itemAdapterPosition = 0
    private var monthToRevision = 0
    private var yearToRevision = 0
    private var dateToRevision = 0L



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subjectListViewModel = ViewModelProvider(this)[SubjectListViewModel::class.java]
        topicViewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        preferenceViewModel = ViewModelProvider(this)[SharedPreferencesViewModel::class.java]
        revisionViewModel = ViewModelProvider(this)[RevisionViewModel::class.java]

        val listenerToggle = object : ToggleClickListener {
            override fun updateToggle(isCompleted: Int, itemId: String, adapterPosition: Int) {
                revisionViewModel.viewModelScope.launch {
                    val revisionItem = revisionViewModel.getAttributesRevision(adapterPosition)
                    revisionViewModel.toggleRevisionInLocal(isCompleted, itemId)
                    revisionViewModel.refreshUiStateRevision(revisionItem.month, revisionItem.year)

                }
            }
        }


        adapter = RevisionListAdapter(listenerToggle)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleViewRevisions.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleViewRevisions.adapter = adapter
        refreshList()

        setUserName()

        setDateOfTheDay()

        revisionViewModel.showStateOnce().observe(viewLifecycleOwner) {

            bindUiState(it)
            binding.textViewRevisionListSize.text = it.revisionList.size.toString()
        }

        openDataPickerDialog()

        saveRevisionItem()

        openRevisionForm()

        exitRevisionForm()

        handlerDragAndSwipe()

        setCurrentMonth()

        handlingMonthChangInList()

        moveListToToday()

        handlerInformativeGuide()
    }


    override fun onResume() {
        super.onResume()
        subjectListViewModel.viewModelScope.launch {
            binding.textViewAmountQuestionsValue.text =
                subjectListViewModel.getAmountOfQuestion().toString()

           binding.textViewPerformanceValue.text = subjectListViewModel.getPerformanceTotal()


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
    private fun handlerInformativeGuide(){
        preferenceViewModel.viewModelScope.launch {

            if (!preferenceViewModel.getStateInformativeGuideUI(requireContext())){
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_home_screen))
                    setMessage(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_home_screen_register))
                    create()
                }.show()
            }
        }

    }
    private fun moveListToToday(){
        binding.textViewMoveToToday.setOnClickListener {

            revisionViewModel.viewModelScope.launch(Dispatchers.Main) {
                refreshList()
                setCurrentMonth()
                delay(250)
                setCurrentDateInPosition(itemAdapterPosition)
            }

        }
    }

    private fun setCurrentDateInPosition(itemAdapterPosition: Int) {

        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = itemAdapterPosition


        val manager = binding.recycleViewRevisions.layoutManager as LinearLayoutManager
        manager.startSmoothScroll(smoothScroller)
    }

    private fun exitRevisionForm() {
        binding.revisionSchedule.textViewBackTo.setOnClickListener {
            binding.revisionSchedule.root.visibility = View.GONE
            hideVirtualKeyboard(requireContext(), binding.revisionSchedule.textViewBtnSave)
            clearFields()
        }

    }

    private fun openRevisionForm() {
        binding.floatingActionButton.setOnClickListener {
            binding.revisionSchedule.root.visibility = View.VISIBLE
        }
    }

    private fun openDataPickerDialog() {
        binding.revisionSchedule.btnCalendar.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            ).show()

        }
    }

    private fun saveRevisionItem() {
        binding.revisionSchedule.textViewBtnSave.setOnClickListener {
            addRevision()
        }
    }

    private fun bindUiState(uiStateRevision: RevisionViewModel.UiStateRevision) {
        adapter.bindVersionLists(uiStateRevision.revisionList)

        val today = DateFormat.format(
            "MM/dd/yyyy",
            Date(Calendar.getInstance().timeInMillis)
        )

        for (index in uiStateRevision.revisionList.indices) {


            val itemDay = DateFormat.format(
                "MM/dd/yyyy",
                Date(uiStateRevision.revisionList[index].date)
            )

            if (today == itemDay && uiStateRevision.revisionList[index].isCompleted == 0) {
                itemAdapterPosition = index
                break
            }

        }
    }


    private fun setUserName() {

        subjectListViewModel.viewModelScope.launch {

            if (preferenceViewModel.getUserName(requireContext()) == "...") {


            }
            val displayName = "${getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_hello_name)} ${
                preferenceViewModel.getUserName(requireContext())
            }!"
            binding.textViewUserName.text = displayName
        }


    }



    private fun setDateOfTheDay() {

        val calendar = Calendar.getInstance()
        binding.textViewDateInHeader.text =
            MyCalendar.formatDateByOneView(
                calendar.time,
                calendar.get(Calendar.DAY_OF_WEEK),
                requireContext()
            )
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        setDateInFormField(calendar)

        dateToRevision = calendar.timeInMillis
        monthToRevision = month
        yearToRevision = year
    }

    private fun setDateInFormField(calendar: Calendar) {

        binding.revisionSchedule.textViewDateRevision.text = MyCalendar.formatDateByOneView(
            calendar.time,
            calendar.get(Calendar.DAY_OF_WEEK),
            requireContext()
        )
    }

    private fun handlingMonthChangInList() {
        var monthInt = Calendar.getInstance().get(Calendar.MONTH)
        var yearInt = Calendar.getInstance().get(Calendar.YEAR)
        binding.textViewLefMonth.setOnClickListener {

            if (monthInt == 0) {
                monthInt = 11
                yearInt -= 1
            } else {
                monthInt -= 1
            }

            refreshListByMonth(monthInt, yearInt)
        }
        binding.textViewRightMonth.setOnClickListener {

            if (monthInt == 11) {
                monthInt = 0
                yearInt += 1
            } else {
                monthInt += 1
            }
            refreshListByMonth(monthInt, yearInt)
        }


    }

    private fun setCurrentMonth() {
        val monthInt = Calendar.getInstance().get(Calendar.MONTH)
        val yearInt = Calendar.getInstance().get(Calendar.YEAR)
        val text = "${stringMonth(monthInt)},$yearInt"
        binding.textViewMonth.text = text

    }

    private fun refreshListByMonth(monthInt: Int, yearInt: Int) {
        revisionViewModel.viewModelScope.launch {
            when (monthInt) {
                0 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                1 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                2 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                3 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                4 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                5 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                6 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                7 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                8 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                9 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                10 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

                11 -> {
                    revisionViewModel.refreshUiStateRevision(monthInt, yearInt)
                    val text = "${stringMonth(monthInt)},$yearInt"
                    binding.textViewMonth.text = text
                }

            }

        }
    }

    private fun stringMonth(month: Int): String {
        val monthh = "ZeroMonth"
        when (month) {
            0 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_january)
            }

            1 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_february)
            }

            2 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_march)
            }

            3 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_april)
            }

            4 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_may)
            }

            5 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_june)
            }

            6 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_july)
            }

            7 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_august)
            }

            8 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_september)
            }

            9 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_october)
            }

            10 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_november)
            }

            11 -> {
                return getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_month_december)
            }
        }
        return monthh
    }

    private fun showDialogDeleteItem(itemPosition: Int) {

        AlertDialog.Builder(requireContext())
            .setTitle(com.ramonpsatu.studyorganizer.core.ui.R.string.text_title_delete_dialog)
            .setMessage(revisionViewModel.getAttributesRevision(itemPosition).title)
            .setPositiveButton(
                requireContext().getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_delete_word)
            ) { _, _ ->


                revisionViewModel.viewModelScope.launch {
                    val item = revisionViewModel.getAttributesRevision(itemPosition)
                    revisionViewModel.deleteRevisionInLocal(item.id)
                    revisionViewModel.refreshUiStateRevision(item.month, item.year)


                }

            }.setNegativeButton(requireContext().getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_msg_no)) { dialog, _ ->

                adapter.notifyItemChanged(itemPosition)
                dialog.dismiss()
            }.show()


    }


    private fun handlerDragAndSwipe() {

        val itemTouch = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlag = ItemTouchHelper.ACTION_STATE_IDLE
                val swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlag, swipeFlag)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                showDialogDeleteItem(viewHolder.adapterPosition)
            }

        }

        ItemTouchHelper(itemTouch).attachToRecyclerView(binding.recycleViewRevisions)
    }

    private fun addRevision() {

        if (!binding.revisionSchedule.textViewDateRevision.text.isNullOrBlank()
            && !binding.revisionSchedule.editTextTitle.text.isNullOrBlank()
        ) {
            revisionViewModel.viewModelScope.launch(Dispatchers.Main) {

                val hours = binding.revisionSchedule.editTextTimeHour.text.toString()
                val minutes = binding.revisionSchedule.editTextTimeMinutes.text.toString()
                var schedule = hours + "h " + minutes + "min"
                var hoursNumber = -1
                var minutesNumber = -1

                if (hours.isNotBlank()) {
                    hoursNumber = hours.toInt()
                }
                if (minutes.isNotBlank()) {
                    minutesNumber = minutes.toInt()
                }


                var error = false
                if (hoursNumber > 24 || minutesNumber > 59) {
                    error = true
                }


                if (error) {

                    toastMessageLong(requireContext(), getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_msg_invalid_time))

                } else {

                    if (hours.isEmpty() && minutes.isEmpty()) {

                        schedule = "--:--"

                    } else if (hours.isEmpty() && minutesNumber == 0) {

                        schedule = "--:--"

                    } else if (hoursNumber == 0 && minutes.isEmpty()) {

                        schedule = "--:--"

                    } else if (hoursNumber == 0 && minutesNumber == 0) {

                        schedule = "--:--"

                    } else if (hours.isEmpty() && minutesNumber in 1..59
                        && (binding.revisionSchedule.radioButtonPm.isChecked
                                || binding.revisionSchedule.radioButtonAm.isChecked)
                    ) {

                        schedule = "00h " + minutes + "min AM"

                    } else if (hours.isEmpty() && minutesNumber in 1..59) {

                        schedule = "00h " + minutes + "min"

                    } else if (hoursNumber in 1..24 && minutes.isEmpty()
                        && binding.revisionSchedule.radioButtonPm.isChecked
                    ) {
                        schedule = hours + "h 00min PM"

                    } else if (hoursNumber in 1..24 && minutes.isEmpty()
                        && binding.revisionSchedule.radioButtonAm.isChecked
                    ) {
                        schedule = hours + "h 00min AM"

                    } else if (hoursNumber in 1..24 && minutes.isEmpty()) {

                        schedule = hours + "h 00min"

                    }


                    val revisionItem = Revision(
                        id = UUID.randomUUID().toString(),
                        title = binding.revisionSchedule.editTextTitle.text.toString(),
                        isCompleted = 0,
                        date = dateToRevision,
                        schedule = schedule,
                        month = monthToRevision,
                        year = yearToRevision

                    )
                    revisionViewModel.addRevisionInLocal(revisionItem)

                    refreshList()
                    binding.revisionSchedule.root.visibility = View.GONE
                    hideVirtualKeyboard(requireContext(), binding.revisionSchedule.textViewBtnSave)
                    clearFields()
                }
            }


        } else {
            toastMessageLong(requireContext(), getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_msg_choose_date_title))
        }

    }

    private fun clearFields() {
        binding.revisionSchedule.textViewDateRevision.text =
            getText(com.ramonpsatu.studyorganizer.core.ui.R.string.text_click_btn_choose_date)
        binding.revisionSchedule.editTextTitle.text?.clear()
        binding.revisionSchedule.editTextTimeHour.text?.clear()
        binding.revisionSchedule.editTextTimeMinutes.text?.clear()
        binding.revisionSchedule.radioGroupAmOrPm.clearCheck()
    }

    private fun refreshList() {
        revisionViewModel.viewModelScope.launch {
            revisionViewModel.refreshUiStateRevision(
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.YEAR)
            )
        }

    }
}