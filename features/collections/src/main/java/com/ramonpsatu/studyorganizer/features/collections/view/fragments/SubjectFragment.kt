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
import com.ramonpsatu.studyorganizer.features.collections.listeners.ToggleClickListener
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.SubjectListAdapter
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentSubjectBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections


/**
 * A [Fragment] that displays a list of subjects.
 */

@AndroidEntryPoint
class SubjectFragment : Fragment() {

    private var _binding: FragmentSubjectBinding? = null

    private val binding get() = _binding!!
    private lateinit var adapter: SubjectListAdapter
    private val scope = CoroutineScope(Dispatchers.Default)

    private lateinit var viewModel: SubjectListViewModel
    private lateinit var preferenceViewModel: SharedPreferencesViewModel
    private var removeComplete = false

    private var requestLink =
        NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/subject_form".toUri())
            .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SubjectListViewModel::class.java]
        ViewModelProvider(this)[SharedPreferencesViewModel::class.java].also {
            preferenceViewModel = it
        }

        val toggleSubject = object : ToggleClickListener {
            override fun updateToggle(isCompleted: Int, itemId: String, adapterPosition: Int) {

                viewModel.viewModelScope.launch {
                    viewModel.updateToggle(isCompleted, itemId)
                    refreshList()
                }

            }


        }


        adapter = SubjectListAdapter(
            { adapterPosition ->
                scope.launch {
                    StateHolderObject.setAttributesSubjectItem(
                        viewModel.getAttributesOfSubject(
                            adapterPosition
                        )
                    )

                }
                findNavController().navigate(requestLink)

            }, { adapterPosition ->

                scope.launch {
                    StateHolderObject.setAttributesSubjectItem(
                        viewModel.getAttributesOfSubject(
                            adapterPosition
                        )
                    )

                }
                findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectFragment_to_topicSearchFragment)

            }, viewModel, toggleSubject
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubjectBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoryRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycleView.adapter = adapter
        StateHolderObject.turn_flag_SaveOrUpdate = false

        preferenceViewModel.viewModelScope.launch {
            removeComplete = preferenceViewModel.getRemoveCompleted(requireContext())
        }
        viewModel.viewModelScope.launch {
            if (removeComplete) {
                viewModel.refreshWithNotCompletedItemsUiState()
            } else {
                viewModel.refreshUiState()
            }
        }


        viewModel.stateOnceAndStream()
            .observe(viewLifecycleOwner) {

                bindUiState(it)
                getSubjectItemSize()
            }

        navigation()


        handlerDragAndSwipe()



        handlerInformativeGuide()
    }

    override fun onResume() {
        super.onResume()
        getSubjectItemSize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private suspend fun refreshList() {


        if (removeComplete) {
            viewModel.refreshWithNotCompletedItemsUiState()
        } else {
            viewModel.refreshUiState()
        }

    }

    private fun handlerInformativeGuide() {
        preferenceViewModel.viewModelScope.launch {

            if (!preferenceViewModel.getStateInformativeGuideUI(requireContext())) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_subjects_word))
                    setMessage(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_subject_screen_register))
                    create()
                }.show()
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
                    viewModel.stateOnceAndStream().value?.subjectItemList!!,
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

        ItemTouchHelper(itemTouch).attachToRecyclerView(binding.categoryRecycleView)
    }

    private fun updateItemPosition() {
        viewModel.stateOnceAndStream()
            .observe(viewLifecycleOwner) {
                for (index in it.subjectItemList.indices) {
                    viewModel.updateSubjectPosition(index, it.subjectItemList[index].id)
                }

            }
    }

    private fun showDialogDeleteItem(itemPosition: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(com.ramonpsatu.studyorganizer.core.ui.R.string.text_title_delete_dialog)
            .setCancelable(false)
            .setMessage(viewModel.getAttributesOfSubject(itemPosition).title)
            .setPositiveButton(
                requireContext().getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_delete_word)
            ) { _, _ ->
                viewModel.viewModelScope.launch {
                    val id = viewModel.getAttributesOfSubject(itemPosition).id

                    viewModel.deleteSubject(id)
                    refreshList()

                }
            }
            .setNegativeButton(requireContext().getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_msg_no)) { dialog, _ ->

                adapter.notifyItemChanged(itemPosition)
                dialog.dismiss()
            }.show()

    }

    private fun setTextInformation(listSize: Int) {
        if (listSize == 0) {
            binding.textViewExhibitionInfor.visibility = View.VISIBLE
        } else {
            binding.textViewExhibitionInfor.visibility = View.INVISIBLE
        }
    }

    private fun bindUiState(uiState: SubjectListViewModel.UiState) {

        val uiStateList = uiState.subjectItemList
        adapter.updateSubjects(uiStateList)

        setTextInformation(uiStateList.size)

    }


    private fun getSubjectItemSize() {
        viewModel.viewModelScope.launch {
            val str = "${viewModel.getCompletedSubjectListSize()}/${viewModel.getSubjectListSize()}"
            binding.textViewSubjectScore.text = str

        }

    }

    private fun navigation() {


        binding.imageViewNewSubjectButton.setOnClickListener {

            StateHolderObject.turn_flag_SaveOrUpdate = true


            findNavController().navigate(requestLink)

        }


    }

}