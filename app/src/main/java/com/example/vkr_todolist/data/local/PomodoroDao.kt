package com.example.vkr_todolist.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.vkr_todolist.data.model.PomodoroTimer
import java.util.Timer
import java.util.concurrent.Flow

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_timer ORDER BY timerId DESC LIMIT 1")
    fun getPomodoroTimer(): LiveData<PomodoroTimer>

    @Insert
    suspend fun insertTimerState(timer: PomodoroTimer)

    @Update
    suspend fun updateTimerState(timer: PomodoroTimer)
}