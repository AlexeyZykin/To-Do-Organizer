package com.example.vkr_todolist.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Int?,

    @ColumnInfo(name="noteTitle")
    var noteTitle: String,

    @ColumnInfo(name="noteDescription")
    var noteDescription: String?,

    @ColumnInfo(name="time")
    var time: String,

    @ColumnInfo(name="listId")
    var listId: Int?,

    @ColumnInfo(name="imagePath")
    var imagePath: String?,
): Serializable