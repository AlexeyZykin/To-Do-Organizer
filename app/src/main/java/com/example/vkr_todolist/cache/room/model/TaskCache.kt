package com.example.vkr_todolist.cache.room.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import java.util.*

@Entity(tableName = CacheConstants.TASK_TABLE_NAME)
data class TaskCache(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "checked")
    var checked: Boolean = false,

    @Embedded
    var list: ListCache,

    @ColumnInfo(name = "date")
    var date: Date?,

    @ColumnInfo("createdDate")
    var createdDate: Date,

    @ColumnInfo(name = "isImportant")
    var isImportant: Boolean = false,

    @ColumnInfo(name = "reminder")
    var reminder: Date?,

    @ColumnInfo(name = "imagePath")
    var imagePath: String?
)