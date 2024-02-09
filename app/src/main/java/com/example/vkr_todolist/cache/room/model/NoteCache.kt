package com.example.vkr_todolist.cache.room.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import java.io.Serializable

@Entity(tableName = CacheConstants.NOTE_TABLE_NAME)
data class NoteCache(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "time")
    var time: String,

    @Embedded
    var list: ListCache,

    @ColumnInfo(name = "imagePath")
    var imagePath: String?,
)