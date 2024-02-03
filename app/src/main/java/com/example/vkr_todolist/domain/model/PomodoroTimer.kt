package com.example.vkr_todolist.domain.model

data class PomodoroTimer(
    val timerId: Int = 0,
    val workTimeInMillis: Long = 25 * 60 * 1000,
    val breakTimeInMillis: Long = 5 * 60 * 1000,
    var currentTimerType: Int = 0, // 0 for work time, 1 for break time
    var timeLeftInMillis: Long = workTimeInMillis,
    var isTimerRunning: Boolean = false,
    var isTimerPaused: Boolean = false
)