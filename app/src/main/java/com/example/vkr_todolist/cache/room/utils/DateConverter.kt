package com.example.vkr_todolist.cache.room.utils

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? = value?.let { Date(it) }
    @TypeConverter
    fun dateToTimeStamp(date: Date?): Long? = date?.time
}


