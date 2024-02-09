package com.example.vkr_todolist.cache.room.dao

import androidx.room.*
import com.example.vkr_todolist.cache.room.model.ListCache
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Query("SELECT * FROM list")
    fun getAllLists(): Flow<List<ListCache>>

    @Query("SELECT * FROM list WHERE listId = :listId")
    suspend fun getListById(listId: Int): ListCache

    @Query("DELETE FROM list WHERE listId = :listId")
    suspend fun deleteList(listId: Int)

    @Insert
    suspend fun insertListItem(listCache: ListCache)

    @Update
    suspend fun updateListItem(listCache: ListCache)
}