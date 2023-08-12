package com.ramonpsatu.studyorganizer.features.collections.domain

import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.core.data.repository.TopicRepository
import javax.inject.Inject


class GetAllTopicsOfTheOneSubjectUseCaseImpl @Inject constructor(private val topicRepository: TopicRepository) :
    GetAllTopicsOfTheOneSubjectUseCase {

    override suspend fun invoke(subjectId: String, date: String): List<TopicItem> {

        return topicRepository.fetchTopic(subjectId, date).map { topicDomain ->
            TopicItem(
                id = topicDomain.id,
                subjectId = topicDomain.date,
                title = topicDomain.title,
                isCompleted = topicDomain.isCompleted,
                date = topicDomain.date,
                description = topicDomain.description,
                performance = topicDomain.performance,
                amountOfQuestions = topicDomain.amountOfQuestions,
                match = topicDomain.match,
                error = topicDomain.error,
                position = topicDomain.position
            )
        }.sortedBy { it.position.inc() }

    }
}