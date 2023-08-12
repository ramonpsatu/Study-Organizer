package com.ramonpsatu.studyorganizer.features.collections.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentTopicDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

//


/**
 * A simple [Fragment] subclass.
 * Use the [TopicDetailsFragment] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TopicDetailsFragment : Fragment() {


    private var _binding: FragmentTopicDetailsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadContent()

        binding.imageViewBack.setOnClickListener {

            binding.root.animate().apply {

                translationXBy(0F)
                translationX(10000F)
                duration = 300
                withEndAction(
                    kotlinx.coroutines.Runnable {

                        parentFragmentManager.beginTransaction().apply {
                            remove(this@TopicDetailsFragment)
                            commit()
                        }

                    }
                )


            }


        }

    }

    private fun loadContent() {
        val topic = StateHolderObject.getTopicDTO()
        binding.textViewDescriptionContent.text = topic.description

        if (binding.textViewDescriptionContent.text == "") {
            binding.textViewDescriptionContent.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_content_description)
        } else {
            binding.textViewDescriptionContent.text =
                StateHolderObject.getTopicDTO().description
        }

        val performance = String.format("%.0f", topic.performance) + "%"
        binding.textViewPerformanceNumber.text = performance
        binding.textViewQuestionsNumber.text = topic.amountOfQuestions.toString()
        binding.textViewErrorNumber.text = topic.error.toString()
        binding.textViewMatchNumber.text = topic.match.toString()


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}