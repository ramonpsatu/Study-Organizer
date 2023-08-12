package com.ramonpsatu.studyorganizer.features.forms


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.ramonpsatu.studyorganizer.core.data.utils.toastMessageShort
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.TopicViewModel
import com.ramonpsatu.studyorganizer.features.forms.databinding.FragmentTopicFormBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

/**
 * A [Fragment] that displays a form of the topic.
 */

@AndroidEntryPoint
class TopicFormFragment : Fragment() {


    private var _binding: FragmentTopicFormBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: TopicViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TopicViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewModelScope.launch {
            viewModel.refreshUiStateOfTopic(
                StateHolderObject.safeArgsSubjectItem().id,
                StateHolderObject.dateTodayForTopic)
        }

        changeTextByScreenEditOrNew()
        navigation()

        setDateOfTheDay()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDateOfTheDay() {

        val dataFormatted = StateHolderObject.dateTodayForTopic
        val year = dataFormatted.substring(0..3).toInt()
        val month = dataFormatted.substring(4..5).toInt() - 1
        val dayOfMonth = dataFormatted.substring(6 until dataFormatted.length).toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        binding.textViewDateOfTopic.text =
            MyCalendar.formatDateByOneView(
                calendar.time,
                StateHolderObject.dayOfWeek,
                requireContext()
            )
    }

    private fun changeTextByScreenEditOrNew() {
        if (StateHolderObject.turn_flag_SaveOrUpdate) {
            binding.textViewTopicWordExhibition.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_new_topic_word)
        } else {
            binding.textViewTopicWordExhibition.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_edit_topic_word)
            binding.textInputLayout.setText(StateHolderObject.getTopicDTO().title)
            binding.textInputEditTextDescription.setText(StateHolderObject.getTopicDTO().description)
            binding.buttonNewTopicSave.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.contentDescription_save_button)
        }
    }

    /**
     * Handles saving or updating of the topics.
     */
    private fun onSaveOrUpdate() {


        if (StateHolderObject.turn_flag_SaveOrUpdate) {

            if (!binding.textInputLayout.text.isNullOrEmpty()) {

                viewModel.viewModelScope.launch {
                    val  id =  UUID.randomUUID().toString()

                    viewModel.addTopic(
                        id,
                        StateHolderObject.safeArgsSubjectItem().id,
                        binding.textInputLayout.text.toString(),
                        0, StateHolderObject.dateTodayForTopic,
                        binding.textInputEditTextDescription.text.toString(),
                        0f, 0, 0, 0,
                    viewModel.getTopicListSize()+1)

                    findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_topicFormFragment_to_topicFragment)

                }
                StateHolderObject.turn_flag_SaveOrUpdate = false
            } else {
                noEmptyFieldsToast()
            }


        } else {

            if (!binding.textInputLayout.text.isNullOrEmpty()) {

                viewModel.viewModelScope.launch {
                    val  id =   StateHolderObject.getTopicDTO().id
                    viewModel.update(
                       id,
                        binding.textInputLayout.text.toString(),
                        binding.textInputEditTextDescription.text.toString()
                    )

                    findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_topicFormFragment_to_topicFragment)
                }

            } else {
                noEmptyFieldsToast()
            }

        }
    }

    private fun noEmptyFieldsToast() {

        toastMessageShort(
            requireContext(),
            getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_toast_topic_form)
        )

    }

    private fun navigation() {


        binding.imageViewBack.setOnClickListener {
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_topicFormFragment_to_topicFragment)

        }



        binding.buttonNewTopicSave.setOnClickListener {

            onSaveOrUpdate()


        }
    }


}