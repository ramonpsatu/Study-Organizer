package com.ramonpsatu.studyorganizer.features.collections.domain

import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem

interface GetAllSubjectUseCase {

    suspend operator fun invoke(): List<SubjectItem>

}