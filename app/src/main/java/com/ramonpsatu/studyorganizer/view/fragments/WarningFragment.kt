package com.ramonpsatu.studyorganizer.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ramonpsatu.studyorganizer.databinding.FragmentWarningBinding
import com.ramonpsatu.studyorganizer.view.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WarningFragment : Fragment() {

    private var _binding: FragmentWarningBinding? = null

    private val binding get() = _binding!!

    private val scope= CoroutineScope(Dispatchers.Main)



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWarningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tbnIGetIt.setOnClickListener {
            scope.launch{

                binding.progressBar.visibility = View.VISIBLE
                binding.textViewInformation.visibility = View.INVISIBLE
                binding.tbnIGetIt.visibility = View.INVISIBLE
                delay(2000)
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}