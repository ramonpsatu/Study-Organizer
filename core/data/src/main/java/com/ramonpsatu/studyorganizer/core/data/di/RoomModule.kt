package com.ramonpsatu.studyorganizer.core.data.di

import android.app.Application
import com.ramonpsatu.studyorganizer.core.data.database.AppDatabase
import com.ramonpsatu.studyorganizer.core.data.database.dao.OffLineRegistrationDAO
import com.ramonpsatu.studyorganizer.core.data.database.dao.RevisionDAO
import com.ramonpsatu.studyorganizer.core.data.database.dao.SubjectDAO
import com.ramonpsatu.studyorganizer.core.data.database.dao.TopicDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object RoomModule {

    @Singleton
    @Provides
    fun providesAppDataBase(application: Application): AppDatabase {
        return AppDatabase.getDatabase(application)
    }

    @Singleton
    @Provides
    fun providesSubjectDAO(appDatabase: AppDatabase): SubjectDAO {
        return appDatabase.subjectDAO()
    }
    @Singleton
    @Provides
    fun providesTopicDAO(appDatabase: AppDatabase): TopicDAO {
        return appDatabase.topicDAO()
    }

    @Singleton
    @Provides
    fun providesOffLineRegistrationDAO(appDatabase: AppDatabase): OffLineRegistrationDAO {
        return appDatabase.offLineRegistrationDAO()
    }

    @Singleton
    @Provides
    fun providesRevisionDAO(appDatabase: AppDatabase): RevisionDAO {
        return appDatabase.revisionDAO()
    }

}