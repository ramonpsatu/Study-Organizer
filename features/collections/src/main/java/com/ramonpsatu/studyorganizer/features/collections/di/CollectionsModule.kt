package com.ramonpsatu.studyorganizer.features.collections.di

import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectUseCase
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectUseCaseImpl
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectsDaysOfWeekUseCase
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllSubjectsDaysOfWeekUseCaseImpl
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllTopicsOfTheOneSubjectUseCase
import com.ramonpsatu.studyorganizer.features.collections.domain.GetAllTopicsOfTheOneSubjectUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class CollectionsModule {

    @Singleton
    @Binds
    abstract fun providesGetAllSubjectsByWeek(impl: GetAllSubjectsDaysOfWeekUseCaseImpl): GetAllSubjectsDaysOfWeekUseCase

    @Singleton
    @Binds
    abstract fun providesGetAllSubjects(impl: GetAllSubjectUseCaseImpl): GetAllSubjectUseCase

    @Singleton
    @Binds
    abstract fun providesGetAllTopicsBySubject(impl: GetAllTopicsOfTheOneSubjectUseCaseImpl): GetAllTopicsOfTheOneSubjectUseCase




}