package com.example.vkr_todolist.cache.room.dao

import androidx.room.*
import com.example.vkr_todolist.cache.room.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE listId = :listId")
    fun getNotesByList(listId: Int): Flow<List<Note>>

    @Query("DELETE FROM note WHERE listId = :listId")
    suspend fun deleteNotesByListId(listId: Int)

    @Query("SELECT * FROM note WHERE noteTitle LIKE :query")
    fun searchNote(query: String): Flow<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)
}