package com.ramonpsatu.studyorganizer.features.collections.domain

import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.core.data.repository.SubjectRepository
import javax.inject.Inject


class GetAllSubjectsDaysOfWeekUseCaseImpl @Inject constructor(private val subjectRepository: SubjectRepository) :
    GetAllSubjectsDaysOfWeekUseCase {


    override suspend fun invoke(): List<List<SubjectItem>> {


        return subjectRepository.getSubjectsDaysOfWeek().map { subjectL ->


            subjectL.map { subjectC ->
                SubjectItem(

                    id = subjectC.id,
                    title = subjectC.title,
                    isCompleted = subjectC.isCompleted,
                    numbersOfTopics = 0,
                    numbersOfTopicsCompleted = 0,
                    backgroundColor = subjectC.backgroundColor,
                    daysOfWeek = subjectC.daysOfWeek,
                    isSelected = subjectC.isSelected,
                    position = subjectC.position

                )


            }.sortedBy { it.position.inc() }


        }


    }


}