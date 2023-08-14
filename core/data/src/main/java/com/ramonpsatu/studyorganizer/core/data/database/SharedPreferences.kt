package com.ramonpsatu.studyorganizer.core.data.database

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences {
    fun sharedInstance(context: Context): SharedPreferences {
        return context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    }
}