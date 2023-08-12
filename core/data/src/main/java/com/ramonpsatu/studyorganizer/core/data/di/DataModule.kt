package com.ramonpsatu.studyorganizer.core.data.di

import com.ramonpsatu.studyorganizer.core.data.database.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
internal object DataModule {


    @Singleton
    @Provides
    fun providesSharedPreferences(): SharedPreferences {
        return SharedPreferences()
    }

}