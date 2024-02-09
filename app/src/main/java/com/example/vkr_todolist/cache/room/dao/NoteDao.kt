package com.example.vkr_todolist.cache.room.dao

import androidx.room.*
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import com.example.vkr_todolist.cache.room.model.EventCache
import com.example.vkr_todolist.cache.room.model.NoteCache
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAllNotes(): Flow<List<NoteCache>>

    @Query("SELECT * FROM ${CacheConstants.NOTE_TABLE_NAME} WHERE listId = :listId")
    fun getNotesByList(listId: Int): Flow<List<NoteCache>>

    @Query("SELECT * FROM ${CacheConstants.NOTE_TABLE_NAME} WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteCache

    @Query("DELETE FROM ${CacheConstants.NOTE_TABLE_NAME} WHERE listId = :listId")
    suspend fun deleteNotesByListId(listId: Int)

    @Query("SELECT * FROM ${CacheConstants.NOTE_TABLE_NAME} WHERE title LIKE :query")
    fun searchNote(query: String): Flow<List<NoteCache>>

    @Query("DELETE FROM ${CacheConstants.NOTE_TABLE_NAME} WHERE id = :id")
    suspend fun deleteNote(id: Int)

    @Insert
    suspend fun insertNote(noteCache: NoteCache)

    @Update
    suspend fun updateNote(noteCache: NoteCache)
}