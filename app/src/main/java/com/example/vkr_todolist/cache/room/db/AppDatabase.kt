package com.example.vkr_todolist.cache.room.db

import android.content.Context
import androidx.room.*
import com.example.vkr_todolist.cache.room.dao.EventDao
import com.example.vkr_todolist.cache.room.dao.ListDao
import com.example.vkr_todolist.cache.room.dao.NoteDao
import com.example.vkr_todolist.cache.room.dao.PomodoroDao
import com.example.vkr_todolist.cache.room.dao.TaskDao
import com.example.vkr_todolist.cache.room.model.Event
import com.example.vkr_todolist.cache.room.model.ListItem
import com.example.vkr_todolist.cache.room.model.Note
import com.example.vkr_todolist.cache.room.model.PomodoroTimer
import com.example.vkr_todolist.cache.room.model.Task
import com.example.vkr_todolist.cache.room.utils.DateConverter


@Database(entities = [ListItem::class, Note::class, Task::class, Event::class, PomodoroTimer::class],
    version=1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase:RoomDatabase() {
    abstract fun getNoteDao(): NoteDao

    abstract fun getTaskDao(): TaskDao

    abstract fun getListDao(): ListDao

    abstract fun getEventDao(): EventDao

    abstract fun getPomodoroDao(): PomodoroDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null
        fun getDataBase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Diary.db"
                ).build()
                instance
            }
        }
    }
}