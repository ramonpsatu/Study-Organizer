package com.ramonpsatu.studyorganizer.core.data.repository

import java.util.TimeZone
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(): CalendarRepository {

    private var daysOfWeek = mutableListOf(0, 1, 2, 3, 4, 5, 6)
    private  var datesOfWeek: MutableList<Long> = mutableListOf(0, 1, 2, 3, 4, 5, 6)
    private  val  dayMondayToSunday = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)

    override suspend fun fetchDaysOfWeek(): List<Int> {
     return selectDaysOfWeek()
    }

    override suspend fun fetchDatesOfWeek(): List<Long> {
        return selectDatesOfWeek()
    }

    private fun selectDaysOfWeek():List<Int>{
        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())
        val day = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val list: List<Int>

        when(day){

            2->{
                list = daysOfWeekStartedMonday()
            }
            3->{
                list = daysOfWeekStartedTuesday()
            }
            4->{
                list = daysOfWeekStartedWednesday()
            }
            5->{
                list = daysOfWeekStartedThursday()
            }
            6->{
                list = daysOfWeekStartedFriday()
            }
            7->{
                list = daysOfWeekStartedSaturday()
            }
            else-> {
                list = daysOfWeekStartedSunday()
            }

        }
        return list
    }

    private fun daysOfWeekStartedMonday(): List<Int> {
        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        if ( dayMondayToSunday == 2) {
            for (day in daysOfWeek.indices) {


                if (day > 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                } else {
                    daysOfWeek[day] = today
                }


            }
        }
        return daysOfWeek
    }

    private fun daysOfWeekStartedTuesday(): List<Int> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)


        if ( dayMondayToSunday  == 3) {
            for (day in daysOfWeek.indices) {


                if ( day == 1){
                    daysOfWeek[day] = today
                }

                if (day == 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                }

                if (day > 1) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                }


            }
        }
        return daysOfWeek
    }

    private fun daysOfWeekStartedWednesday(): List<Int> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)


        if ( dayMondayToSunday  == 4) {
            for (day in daysOfWeek.indices) {


                if (day == 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -2)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 2)
                }

                if (day == 1) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                }

                if ( day == 2){
                    daysOfWeek[day] = today
                }





                if (day > 2) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                }


            }
        }
        return daysOfWeek
    }

    private fun daysOfWeekStartedThursday(): List<Int> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)


        if ( dayMondayToSunday  == 5) {
            for (day in daysOfWeek.indices) {


                if (day == 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -3)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 3)
                }

                if (day == 1) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -2)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 2)
                }

                if (day == 2) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                }

                if ( day == 3){
                    daysOfWeek[day] = today
                }



                if (day > 3) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                }


            }
        }
        return daysOfWeek
    }

    private fun daysOfWeekStartedFriday(): List<Int> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)


        if ( dayMondayToSunday  == 6) {
            for (day in daysOfWeek.indices) {


                if (day == 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -4)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 4)
                }

                if (day == 1) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -3)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 3)
                }

                if (day == 2) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -2)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 2)
                }

                if ( day == 3){
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                }

                if ( day == 4){
                    daysOfWeek[day] = today
                }



                if (day > 4) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                }


            }
        }
        return daysOfWeek
    }

    private fun daysOfWeekStartedSaturday(): List<Int> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)


        if ( dayMondayToSunday  == 7) {
            for (day in daysOfWeek.indices) {


                if (day == 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -5)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 5)
                }

                if (day == 1) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -4)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 4)
                }

                if (day == 2) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -3)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 3)
                }

                if ( day == 3){
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -2)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 2)
                }

                if ( day == 4){
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                }

                if ( day == 5){
                    daysOfWeek[day] = today
                }


                if (day > 5) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                }


            }
        }
        return daysOfWeek
    }

    private fun daysOfWeekStartedSunday(): List<Int> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)


        if ( dayMondayToSunday  == 1) {
            for (day in daysOfWeek.indices) {


                if (day == 0) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -6)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 6)
                }

                if (day == 1) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -5)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 5)
                }

                if (day == 2) {
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -4)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 4)
                }

                if ( day == 3){
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -3)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 3)
                }

                if ( day == 4){
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -2)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 2)
                }

                if ( day == 5){
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
                    daysOfWeek[day] = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                }


                if (day == 6) {
                    daysOfWeek[day] = today

                }


            }
        }
        return daysOfWeek
    }

    private fun selectDatesOfWeek():List<Long>{
        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())
        val day = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val list: List<Long>

        when(day){

            2->{
                list = datesOfWeekStartedMonday()
            }
            3->{
                list = datesOfWeekStartedTuesday()
            }
            4->{
                list = datesOfWeekStartedWednesday()
            }
            5->{
                list = datesOfWeekStartedThursday()
            }
            6->{
                list = datesOfWeekStartedFriday()
            }
            7->{
                list = datesOfWeekStartedSaturday()
            }
            else-> {
                list = datesOfWeekStartedSunday()
            }

        }
        return list
    }

    private fun datesOfWeekStartedMonday(): List<Long> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.timeInMillis

        if (dayMondayToSunday == 2) {
            for (day in datesOfWeek.indices) {


                if (day > 0) {
                    calendar.add(java.util.Calendar.DATE, 1)
                    datesOfWeek[day] = calendar.timeInMillis
                } else {
                    datesOfWeek[day] = today
                }


            }
        }

        return datesOfWeek

    }

    private fun datesOfWeekStartedTuesday(): List<Long> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.timeInMillis



        if ( dayMondayToSunday  == 3) {
            for (day in daysOfWeek.indices) {

                if (day == 0) {
                    calendar.add(java.util.Calendar.DATE, -1)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 1)
                }

                if ( day == 1){
                    datesOfWeek[day] = today
                }

                if (day > 1) {
                    calendar.add(java.util.Calendar.DATE, 1)
                    datesOfWeek[day] = calendar.timeInMillis

                }


            }
        }

        return datesOfWeek

    }

    private fun datesOfWeekStartedWednesday(): List<Long> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.timeInMillis



        if ( dayMondayToSunday  == 4) {
            for (day in daysOfWeek.indices) {

                if (day == 0) {
                    calendar.add(java.util.Calendar.DATE, -2)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 2)
                }

                if ( day == 1){
                    calendar.add(java.util.Calendar.DATE, -1)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 1)
                }

                if ( day == 2){
                    datesOfWeek[day] = today
                }


                if (day > 2) {
                    calendar.add(java.util.Calendar.DATE, 1)
                    datesOfWeek[day] = calendar.timeInMillis

                }


            }
        }
        return datesOfWeek
    }

    private fun datesOfWeekStartedThursday(): List<Long> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.timeInMillis



        if ( dayMondayToSunday  == 5) {
            for (day in daysOfWeek.indices) {

                if (day == 0) {
                    calendar.add(java.util.Calendar.DATE, -3)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 3)
                }

                if ( day == 1){
                    calendar.add(java.util.Calendar.DATE, -2)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 2)
                }

                if ( day == 2){
                    calendar.add(java.util.Calendar.DATE, -1)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 1)
                }
                if ( day == 3){
                    datesOfWeek[day] = today
                }


                if (day > 3) {
                    calendar.add(java.util.Calendar.DATE, 1)
                    datesOfWeek[day] = calendar.timeInMillis

                }


            }
        }
        return datesOfWeek
    }

    private fun datesOfWeekStartedFriday(): List<Long> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.timeInMillis

        if ( dayMondayToSunday   == 6) {
            for (day in daysOfWeek.indices) {

                if (day == 0) {
                    calendar.add(java.util.Calendar.DATE, -4)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 4)
                }

                if ( day == 1){
                    calendar.add(java.util.Calendar.DATE, -3)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 3)
                }

                if ( day == 2){
                    calendar.add(java.util.Calendar.DATE, -2)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 2)
                }
                if ( day == 3){
                    calendar.add(java.util.Calendar.DATE, -1)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 1)
                }

                if ( day == 4){
                    datesOfWeek[day] = today
                }


                if (day > 4) {
                    calendar.add(java.util.Calendar.DATE, 1)
                    datesOfWeek[day] = calendar.timeInMillis

                }


            }
        }
        return datesOfWeek
    }

    private fun datesOfWeekStartedSaturday(): List<Long> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.timeInMillis

        if ( dayMondayToSunday  == 7) {
            for (day in daysOfWeek.indices) {

                if (day == 0) {
                    calendar.add(java.util.Calendar.DATE, -5)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 5)
                }

                if ( day == 1){
                    calendar.add(java.util.Calendar.DATE, -4)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 4)
                }

                if ( day == 2){
                    calendar.add(java.util.Calendar.DATE, -3)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 3)
                }
                if ( day == 3){
                    calendar.add(java.util.Calendar.DATE, -2)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 2)
                }

                if ( day == 4){
                    calendar.add(java.util.Calendar.DATE, -1)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 1)
                }
                if ( day == 5){
                    datesOfWeek[day] = today
                }


                if (day > 5) {
                    calendar.add(java.util.Calendar.DATE, 1)
                    datesOfWeek[day] = calendar.timeInMillis

                }


            }
        }
        return datesOfWeek
    }

    private fun datesOfWeekStartedSunday(): List<Long> {

        val calendar = java.util.Calendar.getInstance(TimeZone.getDefault())

        val today = calendar.timeInMillis

        if ( dayMondayToSunday  == 1) {
            for (day in daysOfWeek.indices) {

                if (day == 0) {
                    calendar.add(java.util.Calendar.DATE, -6)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 6)
                }

                if ( day == 1){
                    calendar.add(java.util.Calendar.DATE, -5)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 5)
                }

                if ( day == 2){
                    calendar.add(java.util.Calendar.DATE, -4)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 4)
                }
                if ( day == 3){
                    calendar.add(java.util.Calendar.DATE, -3)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 3)
                }

                if ( day == 4){
                    calendar.add(java.util.Calendar.DATE, -2)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 2)
                }
                if ( day == 5){
                    calendar.add(java.util.Calendar.DATE, -1)
                    datesOfWeek[day] = calendar.timeInMillis
                    calendar.add(java.util.Calendar.DATE, 1)
                }


                if (day > 5) {
                    datesOfWeek[day] = today

                }


            }
        }
        return datesOfWeek
    }

}