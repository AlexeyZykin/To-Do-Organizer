package com.example.vkr_todolist.cache.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vkr_todolist.cache.room.constants.CacheConstants


@Entity(tableName = CacheConstants.POMODORO_TABLE_NAME)
data class PomodoroTimer(
    @PrimaryKey(autoGenerate = false)
    val timerId: Int = 0,

    @ColumnInfo(name = "workTime")
    val workTimeInMillis: Long = 25 * 60 * 1000,

    @ColumnInfo(name = "breakTime")
    val breakTimeInMillis: Long = 5 * 60 * 1000,

    @ColumnInfo(name = "timerType")
    var currentTimerType: Int = 0, // 0 for work time, 1 for break time

    @ColumnInfo(name = "timeLeft")
    var timeLeftInMillis: Long = workTimeInMillis,

    @ColumnInfo(name = "isTimerRunning")
    var isTimerRunning: Boolean = false,

    @ColumnInfo(name = "isTimerPaused")
    var isTimerPaused: Boolean = false
)