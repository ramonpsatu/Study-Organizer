package com.ramonpsatu.studyorganizer.features.collections.domain

import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem

interface GetAllSubjectsDaysOfWeekUseCase {

    suspend operator fun invoke(): List<List<SubjectItem>>
}