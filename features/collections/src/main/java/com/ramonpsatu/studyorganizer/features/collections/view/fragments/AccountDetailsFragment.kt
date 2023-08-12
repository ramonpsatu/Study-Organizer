package com.ramonpsatu.studyorganizer.features.collections.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ramonpsatu.studyorganizer.core.data.utils.toastMessageShort
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentAccountDetailsBinding
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountDetailsFragment : Fragment() {


    private var _binding: FragmentAccountDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceViewModel: SharedPreferencesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceViewModel = ViewModelProvider(this)[SharedPreferencesViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserNameAndEmail()
        StateHolderObject.configurationFragmentFlag = true
        StateHolderObject.configurationFragmentSwitch = true

        changeUsername()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeUsername() {

        binding.buttonChangeUsername.setOnClickListener {


                if ((binding.usernameDisplay.text.toString().trim()
                            != binding.editTextUsername.text.toString().trim())
                    && !binding.editTextUsername.text.isNullOrBlank()
                ) {



                    preferenceViewModel.viewModelScope.launch {
                        preferenceViewModel.setUserName(
                            requireContext(), binding.editTextUsername.text.toString()
                        )
                    }

                    binding.usernameDisplay.text = binding.editTextUsername.text.toString()

                    binding.editTextUsername.text.clear()
                    toastMessageShort(
                        requireContext(),
                        getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_msg_username_changed)
                    )


                } else {
                    toastMessageShort(
                        requireContext(),
                        getString(com.ramonpsatu.studyorganizer.core.ui.R.string.error_msg_fields_must_be_difference)
                    )
                }

        }

    }



   /* private fun validateEmail(email: String): Boolean {
        //user@d.com
        val user = email.substringBefore("@")
        val domain = email.substringAfter("@")

        val userConditions = user.isNotBlank() && !user.contains("@")

        val domainConditions = domain.length >= 3
                && domain.indexOf(".") >= 1
                && !domain.contains("@")

        val emailConditions = email.indexOf("@") >= 1
                && email.contains("@")
                && email.contains(".")
                && !email.contains(" ")
                && email.indexOf(".") < email.lastIndex - 1



        return userConditions && domainConditions && emailConditions
    }*/



    private fun setUserNameAndEmail() {

        preferenceViewModel.viewModelScope.launch {

            val displayName = preferenceViewModel.getUserName(requireContext())
            binding.usernameDisplay.text = displayName

        }
    }



}