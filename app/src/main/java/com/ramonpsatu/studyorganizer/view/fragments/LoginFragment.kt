package com.ramonpsatu.studyorganizer.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ramonpsatu.studyorganizer.core.data.utils.hideVirtualKeyboard
import com.ramonpsatu.studyorganizer.core.data.utils.toastMessageLong
import com.ramonpsatu.studyorganizer.core.ui.R
import com.ramonpsatu.studyorganizer.databinding.FragmentLoginBinding
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import com.ramonpsatu.studyorganizer.view.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A [Fragment] for the user to log in.
 */

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var preferencesViewModel: SharedPreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesViewModel = ViewModelProvider(this)[SharedPreferencesViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkRegister()
        register(view)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
 private fun checkRegister(){
     preferencesViewModel.viewModelScope.launch {
         if (preferencesViewModel.getUserName(requireContext()) != "...") {
             startActivity(Intent(requireContext(),MainActivity::class.java))
             requireActivity().finish()
         }
     }
 }
    private fun register(view: View){
        binding.imageButtonLogin.setOnClickListener {
            hideVirtualKeyboard(requireContext(), view)

            val username = binding.editTextUsername.text.toString()

            if (username.isNotEmpty()){
                preferencesViewModel.viewModelScope.launch(Dispatchers.Main) {
                    preferencesViewModel.setUserName(requireContext(),username)

                    binding.progressbarLayout.root.visibility = View.VISIBLE
                    delay(2000)

                    parentFragmentManager.beginTransaction().apply {
                        setReorderingAllowed(true)
                        replace(com.ramonpsatu.studyorganizer.R.id.mobile_nav_graph_fragment,WarningFragment())
                    }.commit()

                }

            }else{
                toastMessageLong(requireContext(),getString(R.string.text_warning_field_cannot_null))
            }

        }
    }
}