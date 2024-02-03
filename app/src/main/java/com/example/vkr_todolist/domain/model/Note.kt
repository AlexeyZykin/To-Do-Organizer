package com.example.vkr_todolist.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Note(
    val id: Int?,
    var title: String,
    var description: String?,
    var time: String,
    var list: List,
    var imagePath: String?
)