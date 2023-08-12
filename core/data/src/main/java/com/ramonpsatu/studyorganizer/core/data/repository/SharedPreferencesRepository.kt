package com.ramonpsatu.studyorganizer.core.data.repository

import android.content.Context

interface SharedPreferencesRepository {

    suspend fun setRemoveCompleted(context: Context,switch: Boolean)

    suspend fun getRemoveCompleted(context: Context):Boolean

    suspend fun setPermissionsReadWrite(context: Context,bool: Boolean)

    suspend fun getPermissionsReadWrite(context: Context):Boolean

    suspend fun setCounterRequest(context: Context,counter: Int)

    suspend fun getCounterRequest(context: Context):Boolean

    suspend fun getCounterRequestReturnInt(context: Context):Int

    suspend fun setUserName(context: Context,username:String)

    suspend fun getUserName(context: Context):String

    suspend fun setUserEmail(context: Context,userEmail:String)

    suspend fun getUserEmail(context: Context):String


    suspend fun setShowUiSync(context: Context,show:Boolean)

    suspend fun getShowUiSync(context: Context):Boolean

    suspend fun setInformativeGuideUi(context: Context, show: Boolean)

    suspend fun getStateInformativeGuideUi(context: Context):Boolean

    suspend fun clearPreferences(context: Context)


}