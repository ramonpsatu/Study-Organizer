package com.ramonpsatu.studyorganizer.core.data.di

import com.ramonpsatu.studyorganizer.core.data.repository.CalendarRepository
import com.ramonpsatu.studyorganizer.core.data.repository.CalendarRepositoryImpl
import com.ramonpsatu.studyorganizer.core.data.repository.RevisionRepository
import com.ramonpsatu.studyorganizer.core.data.repository.RevisionRepositoryImpl
import com.ramonpsatu.studyorganizer.core.data.repository.SharedPreferencesRepository
import com.ramonpsatu.studyorganizer.core.data.repository.SharedPreferencesRepositoryImpl
import com.ramonpsatu.studyorganizer.core.data.repository.SubjectRepository
import com.ramonpsatu.studyorganizer.core.data.repository.SubjectRepositoryImpl
import com.ramonpsatu.studyorganizer.core.data.repository.TopicRepository
import com.ramonpsatu.studyorganizer.core.data.repository.TopicRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun providesSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository


    @Singleton
    @Binds
    abstract fun providesTopicRepository(impl: TopicRepositoryImpl): TopicRepository

    @Singleton
    @Binds
    abstract fun providesCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository


    @Singleton
    @Binds
    abstract fun providesPreferencesRepository(impl: SharedPreferencesRepositoryImpl): SharedPreferencesRepository


    @Singleton
    @Binds
    abstract fun providesRevisionRepository(impl: RevisionRepositoryImpl): RevisionRepository

}