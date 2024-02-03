package com.example.vkr_todolist.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeManager {
    const val DEF_DATE_FORMAT="dd MMM, yyyy"
    const val DEF_TIME_FORMAT = "dd MMM, yyyy - HH:mm"
    fun convertDateToString(date: Date): String{
        val df = SimpleDateFormat(DEF_DATE_FORMAT, Locale.getDefault())
        return df.format(date)
    }

    fun convertTimeToString(date: Date): String{
        val tf = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        return tf.format(date)
    }

    fun isDatePassed(taskDate: Date): Boolean {
        val df = SimpleDateFormat(DEF_DATE_FORMAT, Locale.getDefault())
        val currentDate = Date()
        val todayDate = df.parse(df.format(currentDate))
        return taskDate.before(todayDate)
    }

    fun getCurrentDate(): Date{
        val df = SimpleDateFormat(DEF_DATE_FORMAT, Locale.getDefault())
        val currentDate = Date()
        val todayDate = df.parse(df.format(currentDate))
        return todayDate!!
    }

    fun getTomorrowDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val df = SimpleDateFormat(DEF_DATE_FORMAT, Locale.getDefault())
        return df.parse(df.format(calendar.time))!!
    }

    fun getFirstDayWeek(): Date{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val df = SimpleDateFormat(DEF_DATE_FORMAT, Locale.getDefault())
        return df.parse(df.format(calendar.time))!!
    }

    fun getFirstDayMonth(): Date{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val df = SimpleDateFormat(DEF_DATE_FORMAT, Locale.getDefault())
        return df.parse(df.format(calendar.time))!!
    }

    fun getNameMonth(): String{
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("LLLL", Locale.getDefault()).format(calendar.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    fun getDayBeforeDate(date: Date):Date{
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val oneDayBefore = calendar.time
        return oneDayBefore
    }

    fun getHourBeforeDate(date: Date):Date{
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, -1)
        val oneHourBefore = calendar.time
        return oneHourBefore
    }

    fun getHalfHourBeforeDate(date: Date):Date{
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, -30)
        val thirtyMinutesBefore = calendar.time
        return thirtyMinutesBefore
    }

    fun getCurrentDateTime(): Date{
        val df = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        val currentDate = Date()
        val now = df.parse(df.format(currentDate))
        return now!!
    }
}