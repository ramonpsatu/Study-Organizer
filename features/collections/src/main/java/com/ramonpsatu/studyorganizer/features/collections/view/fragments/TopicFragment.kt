package com.ramonpsatu.studyorganizer.features.collections.view.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.TopicViewModel
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.TopicListAdapter
import com.ramonpsatu.studyorganizer.features.collections.listeners.ToggleClickListener
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentTopicListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Collections

/**
 * A [Fragment] that displays a list of topics.
 */
@AndroidEntryPoint
class TopicFragment : Fragment() {

    private var _binding: FragmentTopicListBinding? = null

    private val binding get() = _binding!!


    private lateinit var listenerToggle: ToggleClickListener
    private lateinit var adapter: TopicListAdapter
    private var numberTopics = 0
    private var numberTopicsCompleted = 0

    private val requestLink =
        NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/topic_form".toUri()).build()


    private lateinit var viewModel: TopicViewModel
    private lateinit var preferenceViewModel: SharedPreferencesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        ViewModelProvider(this)[SharedPreferencesViewModel::class.java].also { preferenceViewModel = it }


        listenerToggle = object : ToggleClickListener {

            override fun updateToggle(isCompleted: Int, itemId: String, adapterPosition: Int) {

                viewModel.viewModelScope.launch {
                    viewModel.updateTopicToggle(isCompleted, itemId)
                    viewModel.refreshUiStateOfTopic(
                        StateHolderObject.safeArgsSubjectItem().id,
                        StateHolderObject.dateTodayForTopic
                    )
                    getSubjectItemSize()



                }
            }

        }

        adapter = TopicListAdapter({ adapterPosition ->

            StateHolderObject.setTopicDTO(viewModel.getAttributesOfTopics(adapterPosition))
            findNavController().navigate(requestLink)

        }, { adapterPosition ->

            StateHolderObject.setTopicDTO(viewModel.getAttributesOfTopics(adapterPosition))

            parentFragmentManager.beginTransaction().apply {
                if (savedInstanceState == null) {
                    setCustomAnimations(com.ramonpsatu.studyorganizer.core.ui.R.anim.move_in_enter_right, com.ramonpsatu.studyorganizer.core.ui.R.anim.move_out_exit_right)
                    setReorderingAllowed(true)
                    add(this@TopicFragment.id, DescriptionDialogFragment())
                    commit()
                    addToBackStack(null)
                }

            }

        }, listenerToggle, { adapterPosition ->

            StateHolderObject.setTopicDTO(viewModel.getAttributesOfTopics(adapterPosition))
            parentFragmentManager.beginTransaction().apply {
                if (savedInstanceState == null) {
                    setCustomAnimations(com.ramonpsatu.studyorganizer.core.ui.R.anim.move_in_enter_left, com.ramonpsatu.studyorganizer.core.ui.R.anim.move_out_exit_left)
                    setReorderingAllowed(true)
                    add(this@TopicFragment.id, PerformanceDialogFragment())
                    commit()
                    addToBackStack(null)
                }
            }


        })

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topicRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.topicRecycleView.adapter = adapter
        StateHolderObject.turn_flag_SaveOrUpdate = false

        bindTitleAndBackgroundColor()


        navigationActions()
        viewModel.stateOnce().observe(viewLifecycleOwner) {

            bindUiStateTopic(it)
        }

        viewModel.viewModelScope.launch {
            viewModel.refreshUiStateOfTopic(
                StateHolderObject.safeArgsSubjectItem().id,
                StateHolderObject.dateTodayForTopic
            )

            getSubjectItemSize()
            setDateOfTheDay()
        }
        handlerDragAndSwipe()


        handlerInformativeGuide()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.viewModelScope.cancel()
    }

    private fun handlerInformativeGuide(){

        preferenceViewModel.viewModelScope.launch {

            if (!preferenceViewModel.getStateInformativeGuideUI(requireContext())){
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_name_topics))
                    setMessage(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_topic_screen_register))
                    create()
                }.show()
            }
        }

    }
    private fun showDialogDeleteItem(itemPosition: Int) {

        AlertDialog.Builder(requireContext())
            .setTitle(com.ramonpsatu.studyorganizer.core.ui.R.string.text_title_delete_dialog)
            .setMessage(viewModel.getAttributesOfTopics(itemPosition).title)
            .setPositiveButton(
                requireContext().getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_delete_word)
            ) { _, _ ->

                viewModel.viewModelScope.launch {
                    val id = viewModel.getAttributesOfTopics(itemPosition).id
                    viewModel.delete(id)
                    viewModel.refreshUiStateOfTopic(
                        StateHolderObject.safeArgsSubjectItem().id,
                        StateHolderObject.dateTodayForTopic
                    )
                    getSubjectItemSize()


                }
            }.setNegativeButton(requireContext().getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_msg_no)) { dialog, _ ->

                adapter.notifyItemChanged(itemPosition)
                dialog.dismiss()
            }.show()


    }
    private fun updateItemPosition() {
        viewModel.stateOnce()
            .observe(viewLifecycleOwner) {
                for (index in it.newTopicItem.indices) {
                    viewModel.updateTopicPosition(index, it.newTopicItem[index].id)
                }

            }
    }
    private fun handlerDragAndSwipe() {

        val itemTouch = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlag = ItemTouchHelper.DOWN or ItemTouchHelper.UP
                val swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlag, swipeFlag)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val positionFrom = viewHolder.adapterPosition
                val positionTo = target.adapterPosition
                adapter.notifyItemMoved(positionFrom, positionTo)

                Collections.swap(
                    viewModel.stateOnce().value?.newTopicItem!!,
                    positionFrom,
                    positionTo
                )

                updateItemPosition()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                showDialogDeleteItem(viewHolder.adapterPosition)
            }

        }

        ItemTouchHelper(itemTouch).attachToRecyclerView(binding.topicRecycleView)
    }


    private fun setDateOfTheDay() {

        val dataFormatted = StateHolderObject.dateTodayForTopic
        val year = dataFormatted.substring(0..3).toInt()
        val month = dataFormatted.substring(4..5).toInt() - 1
        val dayOfMonth = dataFormatted.substring(6 until dataFormatted.length).toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        binding.textViewDateInHeader.text =
            MyCalendar.formatDateByOneView(
                calendar.time,
                StateHolderObject.dayOfWeek,
                requireContext()
            )
    }

    private fun bindTitleAndBackgroundColor() {
        val color = StateHolderObject.safeArgsSubjectItem().backgroundColor
        val title = StateHolderObject.safeArgsSubjectItem().title
        binding.textViewExhibitionTitle1.text = title
        binding.textViewExhibitionTitle1.background.setTint(color)
    }

    private fun setTextInformation(listSize: Int) {
        if (listSize == 0) {
            binding.textViewExhibitionInfor.visibility = View.VISIBLE
        } else {
            binding.textViewExhibitionInfor.visibility = View.INVISIBLE
        }
    }


    private fun bindUiStateTopic(uiStateTopic: TopicViewModel.UiStateTopic) {
        adapter.updateTopicList(uiStateTopic.newTopicItem)
        setTextInformation(uiStateTopic.newTopicItem.size)
    }

    /**
     * Show number of the topics.
     */
    private suspend fun getSubjectItemSize() {
        numberTopics = viewModel.getNumbersOfTopics(
            StateHolderObject.safeArgsSubjectItem().id,
            StateHolderObject.dateTodayForTopic
        )

        numberTopicsCompleted =
            viewModel.getNumbersOfTopicsCompleted(
                StateHolderObject.safeArgsSubjectItem().id,
                StateHolderObject.dateTodayForTopic
            )
        val number = "$numberTopicsCompleted/$numberTopics"

        binding.textViewExhibitionScore.text = number
    }

    private fun navigationActions() {

        binding.textViewBack.setOnClickListener {

            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_topicFragment_to_subjectCalendarFragment)

        }

        binding.imageViewButtonAddTopic.setOnClickListener {

            StateHolderObject.turn_flag_SaveOrUpdate = true
            val linkTopicForm =
                NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/topic_form".toUri())
                    .build()
            findNavController().navigate(linkTopicForm)

        }


    }


}