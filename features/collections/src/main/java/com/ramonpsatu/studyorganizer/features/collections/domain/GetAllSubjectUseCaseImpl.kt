package com.ramonpsatu.studyorganizer.features.collections.domain

import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.core.data.repository.SubjectRepository
import javax.inject.Inject

class GetAllSubjectUseCaseImpl @Inject constructor(
    private val subjectRepository: SubjectRepository
) : GetAllSubjectUseCase {


    override suspend fun invoke(): List<SubjectItem> {

        return subjectRepository.fetchSubject().map { subject ->

            SubjectItem(

                id = subject.id,
                title = subject.title,
                isCompleted = subject.isCompleted,
                numbersOfTopics = 0,
                numbersOfTopicsCompleted = 0,
                backgroundColor = subject.backgroundColor,
                daysOfWeek = subject.daysOfWeek,
                isSelected = subject.isSelected,
                position = subject.position


            )

        }.sortedBy { it.position.inc() }


    }
}