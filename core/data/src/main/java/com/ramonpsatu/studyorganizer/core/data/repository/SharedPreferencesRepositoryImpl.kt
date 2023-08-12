package com.ramonpsatu.studyorganizer.core.data.repository

import android.content.Context
import com.ramonpsatu.studyorganizer.core.data.constant.KeysPreference
import com.ramonpsatu.studyorganizer.core.data.database.SharedPreferences
import com.ramonpsatu.studyorganizer.core.data.repository.SharedPreferencesRepository
import javax.inject.Inject

class SharedPreferencesRepositoryImpl @Inject constructor(private val preferences: SharedPreferences) :
    SharedPreferencesRepository {


    override suspend fun setRemoveCompleted(context: Context, switch: Boolean) {

        preferences.sharedInstance(context).edit().putBoolean(KeysPreference.REMOVE_SUBJECTS_DONE, switch).apply()

    }

    override suspend fun getRemoveCompleted(context: Context): Boolean {

        var bool = false
        if (preferences.sharedInstance(context).contains(KeysPreference.REMOVE_SUBJECTS_DONE)) {
            bool = preferences.sharedInstance(context).getBoolean(KeysPreference.REMOVE_SUBJECTS_DONE, false)

        }
        return bool
    }

    override suspend fun setPermissionsReadWrite(context: Context, bool: Boolean) {
        preferences.sharedInstance(context).edit().putBoolean(KeysPreference.PERMISSIONS_GRANTED, bool).apply()

    }

    override suspend fun getPermissionsReadWrite(context: Context): Boolean {
        var bool = false
        if (preferences.sharedInstance(context).contains(KeysPreference.PERMISSIONS_GRANTED)) {
            bool = preferences.sharedInstance(context).getBoolean(KeysPreference.PERMISSIONS_GRANTED, false)

        }
        return bool
    }

    override suspend fun setCounterRequest(context: Context, counter: Int) {
        preferences.sharedInstance(context).edit().putInt(KeysPreference.COUNTER_REQUEST, counter).apply()
    }

    override suspend fun getCounterRequest(context: Context): Boolean {

        var bool = false
        if (preferences.sharedInstance(context).contains(KeysPreference.COUNTER_REQUEST)) {
            bool = true

        }
        return bool
    }

    override suspend fun getCounterRequestReturnInt(context: Context): Int {

        var counter = 0
        if (preferences.sharedInstance(context).contains(KeysPreference.COUNTER_REQUEST)) {
            counter = preferences.sharedInstance(context).getInt(KeysPreference.COUNTER_REQUEST, 0)

        }
        return counter
    }

    override suspend fun setUserName(context: Context, username: String) {
        preferences.sharedInstance(context).edit().putString(KeysPreference.USER_NAME, username).apply()

    }

    override suspend fun getUserName(context: Context): String {
        return preferences.sharedInstance(context).getString(KeysPreference.USER_NAME, "...")!!
    }

    override suspend fun setUserEmail(context: Context, userEmail: String) {
        preferences.sharedInstance(context).edit().putString(KeysPreference.USER_EMAIL, userEmail).apply()
    }

    override suspend fun getUserEmail(context: Context): String {
        return preferences.sharedInstance(context).getString(KeysPreference.USER_EMAIL, "...")!!
    }

    override suspend fun setShowUiSync(context: Context, show: Boolean) {
        preferences.sharedInstance(context).edit().putBoolean(KeysPreference.SHOW_SYNC_UI, show).apply()
    }

    override suspend fun getShowUiSync(context: Context): Boolean {
        var bool = false
        if (preferences.sharedInstance(context).contains(KeysPreference.SHOW_SYNC_UI)) {
            bool = preferences.sharedInstance(context).getBoolean(KeysPreference.SHOW_SYNC_UI, false)

        }
        return bool
    }

    override suspend fun setInformativeGuideUi(context: Context, show: Boolean) {
        preferences.sharedInstance(context).edit().putBoolean(KeysPreference.SHOW_INFORMATIVE_GUIDE_UI, show).apply()
    }

    override suspend fun getStateInformativeGuideUi(context: Context): Boolean {
        var bool = false
        if (preferences.sharedInstance(context).contains(KeysPreference.SHOW_INFORMATIVE_GUIDE_UI)) {
            bool = preferences.sharedInstance(context).getBoolean(KeysPreference.SHOW_INFORMATIVE_GUIDE_UI, false)

        }
        return bool
    }

    override suspend fun clearPreferences(context: Context) {
        preferences.sharedInstance(context).edit().clear().apply()
    }


}