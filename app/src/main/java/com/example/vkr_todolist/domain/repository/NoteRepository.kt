package com.example.vkr_todolist.domain.repository

import com.example.vkr_todolist.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun getNote(id: Int): Note
    suspend fun getAllNotes(): Flow<List<Note>>
    suspend fun getNotesByList(listId: Int): Flow<List<Note>>
    suspend fun searchNote(query: String): Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Int)
}

