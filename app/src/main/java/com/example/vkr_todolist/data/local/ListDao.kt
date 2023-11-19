package com.example.vkr_todolist.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.vkr_todolist.data.model.ListItem
import com.example.vkr_todolist.data.model.Note
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