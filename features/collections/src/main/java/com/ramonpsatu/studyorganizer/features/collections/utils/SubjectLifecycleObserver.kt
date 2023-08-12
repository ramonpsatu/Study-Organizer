package com.ramonpsatu.studyorganizer.features.collections.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel

class SubjectLifecycleObserver(
    private val viewModel: SubjectListViewModel
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewModel.onResume()

    }
}