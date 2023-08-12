package com.ramonpsatu.studyorganizer.features.collections.domain

import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem

interface GetAllTopicsOfTheOneSubjectUseCase {

    suspend operator fun invoke(subjectId: String, date: String): List<TopicItem>
}