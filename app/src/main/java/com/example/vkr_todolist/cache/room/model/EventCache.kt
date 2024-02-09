package com.example.vkr_todolist.cache.room.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import java.io.Serializable
import java.util.*

@Entity(tableName = CacheConstants.Event_TABLE_NAME)
data class EventCache(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "title")
    var title: String,

    @Embedded
    var list: ListCache,

    @ColumnInfo(name = "date")
    var date: Date,

    @ColumnInfo(name = "reminder")
    var reminder: Date?,

    @ColumnInfo(name = "isFinished")
    var isFinished: Boolean = false,
)