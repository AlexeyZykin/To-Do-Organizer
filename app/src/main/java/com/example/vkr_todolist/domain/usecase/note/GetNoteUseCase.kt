package com.example.vkr_todolist.domain.usecase.note

import com.example.vkr_todolist.domain.model.Note
import com.example.vkr_todolist.domain.repository.NoteRepository

class GetNoteUseCase(private val noteRepository: NoteRepository) {
    suspend fun invoke(id: Int): Note {
        return noteRepository.getNote(id)
    }
}