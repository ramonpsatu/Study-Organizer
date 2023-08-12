package com.ramonpsatu.studyorganizer.features.collections.view.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ramonpsatu.studyorganizer.core.data.utils.toastMessageShort
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.TopicViewModel
import com.ramonpsatu.studyorganizer.features.collections.databinding.PerformanceDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PerformanceDialogFragment : Fragment() {

    private var _binding: PerformanceDialogFragmentBinding? = null
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
        _binding = PerformanceDialogFragmentBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {

            viewModel.viewModelScope.launch {

                val error: Int
                val match: Int
                val amountOfQuestions: Int
                val performance: Float


                if (!binding.editTextError.text.isNullOrBlank()
                    && !binding.editTextMatch.text.isNullOrBlank()
                ) {
                    error = binding.editTextError.text.toString().toInt()
                    match = binding.editTextMatch.text.toString().toInt()

                    amountOfQuestions = error + match
                    performance = (match / (amountOfQuestions).toFloat()) * 100

                    val condition = binding.editTextMatch.text.isNotEmpty() && match > 0

                    if (amountOfQuestions > 0 && condition) {

                        viewModel.updatePerformance(
                            StateHolderObject.getTopicDTO().id,
                            performance, amountOfQuestions, match, error
                        )



                        binding.root.animate().apply {

                            translationX(-10000F)
                            alpha(0F)
                            duration = 100
                            withEndAction(
                                kotlinx.coroutines.Runnable {
                                    parentFragmentManager.beginTransaction().apply {
                                        setReorderingAllowed(true)
                                        replace(this@PerformanceDialogFragment.id, TopicFragment())
                                        commit()
                                    }

                                }
                            )

                        }


                    } else {

                        toastMessageShort(
                            requireContext(),
                            getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_field_cannot_null)
                        )

                    }

                } else {

                    toastMessageShort(
                        requireContext(),
                        getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_field_cannot_null)
                    )

                }





            }

        }

        binding.textViewBack.setOnClickListener {

            binding.root.animate().apply {
                translationX(-10000F)
                alpha(0F)
                duration = 300
                withEndAction(
                    kotlinx.coroutines.Runnable {
                        parentFragmentManager.beginTransaction().apply {
                            setReorderingAllowed(true)
                            remove(this@PerformanceDialogFragment)
                            commit()
                        }
                    }
                )
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}