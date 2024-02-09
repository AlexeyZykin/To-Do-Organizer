package com.example.vkr_todolist.data.source.local

import com.example.vkr_todolist.data.model.NoteEntity
import kotlinx.coroutines.flow.Flow


interface NoteCacheDataSource {
    suspend fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun getNotesByList(listId: Int): Flow<List<NoteEntity>>
    suspend fun getNote(id: Int): NoteEntity
    suspend fun deleteNotesByListId(listId: Int)
    suspend fun searchNote(query: String): Flow<List<NoteEntity>>
    suspend fun addNote(noteEntity: NoteEntity)
    suspend fun updateNote(noteEntity: NoteEntity)
    suspend fun deleteNote(id: Int)
}
