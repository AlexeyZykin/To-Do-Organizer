package com.example.vkr_todolist.data.source.local.db

import android.content.Context
import androidx.room.*
import com.example.vkr_todolist.data.source.local.DateConverter
import com.example.vkr_todolist.data.source.local.dao.*
import com.example.vkr_todolist.data.source.local.model.*


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