package com.ramonpsatu.studyorganizer.features.collections.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentSettingsBinding
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedPreferencesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[SharedPreferencesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedViewModel.viewModelScope.launch {
            binding.switchRemoveSubejctsDone.isChecked = sharedViewModel.getRemoveCompleted(requireContext())
            binding.switchInformativeGuide.isChecked = sharedViewModel.getStateInformativeGuideUI(requireContext())


        }
        backScreen()
        removeSubjectsDone()
        removeInformativeGuideUI()
        navToAccountDetails()


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        StateHolderObject.configurationFragmentSwitch = true

    }



    private fun removeSubjectsDone() {
        binding.viewTouchableRemoveSubjectsDone.setOnClickListener {
            binding.switchRemoveSubejctsDone.isChecked = !binding.switchRemoveSubejctsDone.isChecked
            sharedViewModel.setRemoveCompleted(requireContext(), binding.switchRemoveSubejctsDone.isChecked)

        }
    }

    private fun removeInformativeGuideUI() {
        binding.viewTouchableRemoveInformativeGuide.setOnClickListener {
            binding.switchInformativeGuide.isChecked = !binding.switchInformativeGuide.isChecked
            sharedViewModel.setStateInformativeGuideUI(requireContext(), binding.switchInformativeGuide.isChecked)

        }
    }

    private fun backScreen() {
        binding.textViewBack.setOnClickListener {

            StateHolderObject.configurationFragmentFlag = true
            StateHolderObject.configurationFragmentSwitch = true

            binding.root.animate().apply {

                translationYBy(binding.root.pivotY)
                translationY(10000F)
                duration = 600
                withEndAction(
                    kotlinx.coroutines.Runnable {

                        parentFragmentManager.beginTransaction().apply {
                            setReorderingAllowed(true)
                            remove(this@SettingsFragment)
                            commit()
                            addToBackStack(null)

                        }
                    }
                )

            }

        }

    }




    private fun navToAccountDetails() {
        binding.viewTouchableAccountDetails.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                setReorderingAllowed(true)
                remove(this@SettingsFragment)
                commit()

            }
            val linkToAccountDetails =
                NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/account_details".toUri())
                    .build()
            findNavController().navigate(linkToAccountDetails)


        }
    }


}