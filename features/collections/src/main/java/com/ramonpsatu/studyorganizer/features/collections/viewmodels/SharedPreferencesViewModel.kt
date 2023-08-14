package com.ramonpsatu.studyorganizer.features.collections.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramonpsatu.studyorganizer.core.data.repository.SharedPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedPreferencesViewModel @Inject constructor(
    private val preferencesRepository: SharedPreferencesRepository
) : ViewModel() {


    fun setRemoveCompleted(context: Context, switch: Boolean) {

        viewModelScope.launch {
            preferencesRepository.setRemoveCompleted(context, switch)
        }

    }

    suspend fun getRemoveCompleted(context: Context): Boolean {
        return preferencesRepository.getRemoveCompleted(context)
    }

    fun setPermissionsReadWrite(context: Context, bool: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPermissionsReadWrite(context, bool)
        }

    }

    fun getPermissionsReadWrite(context: Context): Boolean {
        var bool = false

        viewModelScope.launch {
            bool = preferencesRepository.getPermissionsReadWrite(context)
        }

        return bool
    }

    fun setCounterRequest(context: Context, counter: Int) {
        viewModelScope.launch {
            preferencesRepository.setCounterRequest(context, counter)
        }
    }

    fun getCounterRequest(context: Context): Boolean {
        var bool = false

        viewModelScope.launch {
            bool = preferencesRepository.getCounterRequest(context)
        }

        return bool
    }

    fun getCounterRequestReturnInt(context: Context): Int {
        var counter = 0

        viewModelScope.launch {
            counter = preferencesRepository.getCounterRequestReturnInt(context)
        }

        return counter
    }

    fun setStateInformativeGuideUI(context: Context, show: Boolean) {

        viewModelScope.launch {
            preferencesRepository.setInformativeGuideUi(context, show)
        }
    }


    suspend fun setUserName(context: Context, username: String) {
        preferencesRepository.setUserName(context, username)
    }


    suspend fun getUserName(context: Context): String {
        return preferencesRepository.getUserName(context)
    }




    suspend fun getStateInformativeGuideUI(context: Context): Boolean {
        return preferencesRepository.getStateInformativeGuideUi(context)
    }



}