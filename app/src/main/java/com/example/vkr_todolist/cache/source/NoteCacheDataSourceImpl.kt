package com.example.vkr_todolist.cache.source

import com.example.vkr_todolist.cache.room.dao.NoteDao
import com.example.vkr_todolist.cache.room.mapper.NoteCacheMapper
import com.example.vkr_todolist.data.model.NoteEntity
import com.example.vkr_todolist.data.source.local.NoteCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteCacheDataSourceImpl(
    private val noteDao: NoteDao,
    private val noteCacheMapper: NoteCacheMapper
) : NoteCacheDataSource {
    override suspend fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
            .map { it.map { note -> noteCacheMapper.mapFromCache(note) } }
    }

    override suspend fun getNotesByList(listId: Int): Flow<List<NoteEntity>> {
        return noteDao.getNotesByList(listId)
            .map { it.map { note -> noteCacheMapper.mapFromCache(note) } }
    }

    override suspend fun getNote(id: Int): NoteEntity {
        return noteCacheMapper.mapFromCache(noteDao.getNoteById(id))
    }

    override suspend fun deleteNotesByListId(listId: Int) {
        noteDao.deleteNotesByListId(listId)
    }

    override suspend fun searchNote(query: String): Flow<List<NoteEntity>> {
        return noteDao.searchNote(query)
            .map { it.map { note -> noteCacheMapper.mapFromCache(note) } }
    }

    override suspend fun addNote(noteEntity: NoteEntity) {
        noteDao.insertNote(noteCacheMapper.mapToCache(noteEntity))
    }

    override suspend fun updateNote(noteEntity: NoteEntity) {
        noteDao.updateNote(noteCacheMapper.mapToCache(noteEntity))
    }

    override suspend fun deleteNote(id: Int) {
        noteDao.deleteNote(id)
    }
}