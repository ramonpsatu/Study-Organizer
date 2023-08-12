package com.ramonpsatu.studyorganizer.features.collections.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.databinding.TopicDescriptionLayoutBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * A [DialogFragment] that displays a description of the topic.
 */
@AndroidEntryPoint
class DescriptionDialogFragment : Fragment() {

    private var _binding: TopicDescriptionLayoutBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TopicDescriptionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDescription()
        binding.imageViewButtonBack.setOnClickListener {

            binding.root.animate().apply {
                translationXBy(0F)
                translationX(10000F)
                alpha(0F)
                duration = 300
                withEndAction(
                    kotlinx.coroutines.Runnable {
                        parentFragmentManager.beginTransaction().setReorderingAllowed(true)
                            .remove(this@DescriptionDialogFragment).commit()

                    }
                )
            }


        }
    }

    private fun loadDescription() {
        binding.textViewDescriptionContent.text = StateHolderObject.getTopicDTO().description

        if (binding.textViewDescriptionContent.text == "") {
            binding.textViewDescriptionContent.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_content_description)
        } else {
            binding.textViewDescriptionContent.text =
                StateHolderObject.getTopicDTO().description
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}