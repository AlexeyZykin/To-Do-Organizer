package com.example.vkr_todolist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="list")
data class ListItem (
    @PrimaryKey(autoGenerate = true)
    val listId:Int?,

    @ColumnInfo(name="listTitle")
    val listTitle: String,

    @ColumnInfo(name="listColor")
    val listColor: Int,

    @ColumnInfo(name="allItemCounter")
    val allItemCounter: Int,

    @ColumnInfo(name="checkedItemsCounter")
    val checkedItemsCounter: Int,

    @ColumnInfo(name="itemsIds")
    val itemsIds: String,
): Serializable
