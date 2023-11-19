package com.example.vkr_todolist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int?,

    @ColumnInfo(name="taskTitle")
    var taskTitle: String,

    @ColumnInfo(name="taskDescription")
    var taskDescription: String?,

    @ColumnInfo(name="taskChecked")
    var taskChecked: Boolean=false,

    @ColumnInfo(name="listId")
    var listId: Int?,

    @ColumnInfo(name="date")
    var date: Date?,

    @ColumnInfo("createdDate")
    var createdDate: Date?,

    @ColumnInfo(name="isImportant")
    var isImportant: Boolean=false,

    @ColumnInfo(name="reminder")
    var reminder: Date?,

    @ColumnInfo(name="imagePath")
    var imagePath: String?
):java.io.Serializable