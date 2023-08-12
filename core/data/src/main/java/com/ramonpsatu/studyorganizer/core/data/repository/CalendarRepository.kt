package com.ramonpsatu.studyorganizer.core.data.repository

interface CalendarRepository {


    suspend fun fetchDaysOfWeek():List<Int>

    suspend fun fetchDatesOfWeek():List<Long>

}