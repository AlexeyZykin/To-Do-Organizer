package com.example.vkr_todolist.domain.usecase.note

import com.example.vkr_todolist.domain.model.Note
import com.example.vkr_todolist.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class SearchNoteUseCase(private val noteRepository: NoteRepository) {
    suspend fun invoke(query: String): Flow<List<Note>> {
        return noteRepository.searchNote(query)
    }
}