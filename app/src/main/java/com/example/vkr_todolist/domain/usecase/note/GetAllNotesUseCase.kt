package com.example.vkr_todolist.domain.usecase.note

import com.example.vkr_todolist.domain.model.Note
import com.example.vkr_todolist.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(private val noteRepository: NoteRepository) {
    suspend fun invoke(): Flow<List<Note>> {
        return noteRepository.getAllNotes()
    }
}