package com.example.vkr_todolist.cache.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import java.io.Serializable

@Entity(tableName = CacheConstants.List_TABLE_NAME)
data class ListCache(
    @PrimaryKey(autoGenerate = true)
    val listId: Int?,

    @ColumnInfo(name = "listTitle")
    val title: String
) : Serializable
