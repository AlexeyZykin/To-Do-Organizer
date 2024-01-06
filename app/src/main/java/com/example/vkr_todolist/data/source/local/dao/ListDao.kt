package com.example.vkr_todolist.data.source.local.dao

import androidx.room.*
import com.example.vkr_todolist.data.source.local.model.ListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Query("SELECT * FROM list")
    fun getAllLists(): Flow<List<ListItem>>

    @Query("SELECT * FROM list WHERE listId = :listId")
    fun getListById(listId: Int): Flow<List<ListItem>>

    @Query("DELETE FROM list WHERE listId = :listId")
    suspend fun deleteList(listId: Int)

    @Insert
    suspend fun insertListItem(listItem: ListItem)

    @Update
    suspend fun updateListItem(listItem: ListItem)
}