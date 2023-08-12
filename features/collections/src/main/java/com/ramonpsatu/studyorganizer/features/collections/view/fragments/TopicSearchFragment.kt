package com.ramonpsatu.studyorganizer.features.collections.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramonpsatu.studyorganizer.core.data.utils.hideVirtualKeyboard
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentTopicSearchBinding
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.TopicSearchListAdapter
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.TopicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


/**
 * A [Fragment] that displays a list of topics by day of a subject.
 */

@AndroidEntryPoint
class TopicSearchFragment : Fragment() {


    private var _binding: FragmentTopicSearchBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: TopicSearchListAdapter
    private lateinit var topicList: List<TopicItem>

    private var topicListSize = 0
    private var jobResearch = CoroutineScope(Dispatchers.Main)


    private lateinit var viewModel: TopicViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        adapter = TopicSearchListAdapter { viewId ->
            StateHolderObject.setTopicDTO(viewModel.getAttributesTopics(viewId))

            parentFragmentManager.beginTransaction().apply {
                if (savedInstanceState == null) {
                    setCustomAnimations(com.ramonpsatu.studyorganizer.core.ui.R.anim.move_in_enter_right, com.ramonpsatu.studyorganizer.core.ui.R.anim.move_out_exit_right)
                    setReorderingAllowed(true)
                    add(this@TopicSearchFragment.id, TopicDetailsFragment())
                    commit()
                    addToBackStack(null)
                }

            }

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topicRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.topicRecycleView.adapter = adapter

        navigate()
        setSubjectTitleAndColor()
        research()


        binding.imageViewButtonTaskSearch.setOnClickListener {

            viewModel.stateOnceAllTopicsBySubject().observe(viewLifecycleOwner) {
                bindUiState(it)
            }

        }


        viewModel.viewModelScope.launch {
            viewModel.refreshUiStateAllTopicsBySubject(
                StateHolderObject.safeArgsSubjectItem().id
            )
        }


        viewModel.stateOnceAllTopicsBySubject().observe(viewLifecycleOwner) {
            bindUiState(it)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        if (jobResearch.isActive) {
            jobResearch.cancel()
        }
    }

    private fun research() {

        binding.ediTextResearchTopics.setOnEditorActionListener { v, actionId, _ ->
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                val topicItems = viewModel.research(binding.ediTextResearchTopics, topicList)
                topicListSize = topicItems.size

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    hideVirtualKeyboard(requireContext(), v)
                    adapter.updateTopicList(topicItems)
                    setCompletedTotalItems(topicItems)
                    notifyIfListIsEmpty()
                }

            }
            true
        }


    }


    private fun bindUiState(UiStateTopicsBySubject: TopicViewModel.UiStateTopicsBySubject) {
        topicListSize = UiStateTopicsBySubject.allTopicsBySubject.size
        topicList = UiStateTopicsBySubject.allTopicsBySubject
        adapter.updateTopicList(UiStateTopicsBySubject.allTopicsBySubject)


        setCompletedTotalItems(topicList)


        notifyIfListIsEmpty()


    }

    private fun setCompletedTotalItems(list: List<TopicItem>) {
        var completed = 0


        for (index in 0 until topicListSize) {

            if (list[index].isCompleted == 1) {

                completed += 1
            }
        }
        val score = "$completed/$topicListSize"
        binding.textViewTopicScore.text = score
    }

    private fun notifyIfListIsEmpty() {
        if (topicListSize == 0) {

            binding.textViewCategoryExhibition15.visibility = View.VISIBLE
            binding.textViewCategoryExhibition15.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_no_topic_for_this_subject)

        } else {
            binding.textViewCategoryExhibition15.visibility = View.INVISIBLE
        }
    }

    private fun setSubjectTitleAndColor() {
        val color = StateHolderObject.safeArgsSubjectItem().backgroundColor
        val title = StateHolderObject.safeArgsSubjectItem().title
        binding.tetViewSubjectTitleResource.text = title
        binding.tetViewSubjectTitleResource.background.setTint(color)
    }

    private fun navigate() {
        binding.imageViewButtonBackCalendar.setOnClickListener {


            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_topicSearchFragment_to_subjectFragment)


        }
    }


}