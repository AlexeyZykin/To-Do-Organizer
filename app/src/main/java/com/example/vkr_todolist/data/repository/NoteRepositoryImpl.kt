package com.example.vkr_todolist.data.repository

import com.example.vkr_todolist.data.mapper.NoteEntityMapper
import com.example.vkr_todolist.data.source.local.NoteCacheDataSource
import com.example.vkr_todolist.domain.model.Note
import com.example.vkr_todolist.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteEntityMapper: NoteEntityMapper
) : NoteRepository {
    override suspend fun getNote(id: Int): Note {
        return noteEntityMapper.mapFromEntity(noteCacheDataSource.getNote(id))
    }

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return noteCacheDataSource.getAllNotes()
            .map { it.map { note -> noteEntityMapper.mapFromEntity(note) } }
    }

    override suspend fun getNotesByList(listId: Int): Flow<List<Note>> {
        return noteCacheDataSource.getNotesByList(listId)
            .map { it.map { note-> noteEntityMapper.mapFromEntity(note) } }
    }

    override suspend fun searchNote(query: String): Flow<List<Note>> {
        return noteCacheDataSource.searchNote(query)
            .map { it.map { note -> noteEntityMapper.mapFromEntity(note) } }
    }

    override suspend fun addNote(note: Note) {
        noteCacheDataSource.addNote(noteEntityMapper.mapToEntity(note))
    }

    override suspend fun updateNote(note: Note) {
        noteCacheDataSource.updateNote(noteEntityMapper.mapToEntity(note))
    }

    override suspend fun deleteNote(id: Int) {
        noteCacheDataSource.deleteNote(id)
    }
}