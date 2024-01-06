package com.example.vkr_todolist.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "event")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int?,

    @ColumnInfo(name="eventTitle")
    var eventTitle: String,

    @ColumnInfo(name="listId")
    var listId: Int?,

    @ColumnInfo(name="listName")
    var listName: String,

    @ColumnInfo(name="date")
    var date: Date?,

    @ColumnInfo(name="reminder")
    var reminder: Date?,

    @ColumnInfo(name="isFinished")
    var isFinished: Boolean = false,
    ): Serializable