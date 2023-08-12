package com.ramonpsatu.studyorganizer.features.collections.utils

import android.content.Context
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar.day
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar.dayOfWeek
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar.maxDaysOfMonth
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar.maxDaysOfYear
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar.month
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar.numberOfDayInMonth
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar.year
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * [MyCalendar] calendar instance.
 * @property dayOfWeek returns the day of the week.
 * @property month returns the month of the year.
 * @property year returns the number of the current year.
 * @property day  day state holder in calendar.
 * @property maxDaysOfMonth returns the maximum number of days in the month.
 * @property maxDaysOfYear returns the maximum number of days of the year.
 * @property numberOfDayInMonth returns the number representing the day of the month.
 */
object MyCalendar {

    val calendarDay: Calendar = Calendar.getInstance(TimeZone.getDefault())
    val numberOfDayInMonth = calendarDay.get(Calendar.DAY_OF_MONTH)
    val dayOfWeek = calendarDay.get(Calendar.DAY_OF_WEEK)
    val month = calendarDay.get(Calendar.MONTH)
    var year = calendarDay.get(Calendar.YEAR)
    val maxDaysOfYear = calendarDay.getActualMaximum(Calendar.DAY_OF_YEAR)
    val maxDaysOfMonth = calendarDay.getActualMaximum(Calendar.DAY_OF_MONTH)
    var day: Int? = null

    fun formatDateByOneView(
        date: Date, dayOfWeek: Int, context: Context
    ): String {

        var dayOfWeekName = ""
        val dataFormat = android.text.format.DateFormat.getLongDateFormat(context)

        when (dayOfWeek) {

            1 -> {

                dayOfWeekName =
                    context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_sunday)


            }

            2 -> {

                dayOfWeekName =
                    context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_monday)


            }
            3 -> {

                dayOfWeekName =
                    context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_tuesday)


            }
            4 -> {

                dayOfWeekName =
                    context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_wednesday)


            }
            5 -> {

                dayOfWeekName =
                    context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_thursday)


            }
            6 -> {

                dayOfWeekName =
                    context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_friday)


            }
            7 -> {

                dayOfWeekName =
                    context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_saturday)


            }


        }



        return "$dayOfWeekName ${dataFormat.format(date)}."
    }

}