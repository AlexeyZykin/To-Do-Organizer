package com.example.vkr_todolist.cache.room.db

import android.content.Context
import androidx.room.*
import com.example.vkr_todolist.cache.room.dao.EventDao
import com.example.vkr_todolist.cache.room.dao.ListDao
import com.example.vkr_todolist.cache.room.dao.NoteDao
import com.example.vkr_todolist.cache.room.dao.PomodoroDao
import com.example.vkr_todolist.cache.room.dao.TaskDao
import com.example.vkr_todolist.cache.room.model.EventCache
import com.example.vkr_todolist.cache.room.model.ListCache
import com.example.vkr_todolist.cache.room.model.NoteCache
import com.example.vkr_todolist.cache.room.model.PomodoroTimer
import com.example.vkr_todolist.cache.room.model.TaskCache
import com.example.vkr_todolist.cache.room.utils.DateConverter


@Database(entities = [ListCache::class, NoteCache::class, TaskCache::class, EventCache::class, PomodoroTimer::class],
    version=1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase:RoomDatabase() {
    abstract fun getNoteDao(): NoteDao

    abstract fun getTaskDao(): TaskDao

    abstract fun getListDao(): ListDao

    abstract fun getEventDao(): EventDao

    abstract fun getPomodoroDao(): PomodoroDao

}