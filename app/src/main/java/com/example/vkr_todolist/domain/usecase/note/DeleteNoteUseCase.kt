package com.example.vkr_todolist.domain.usecase.note

import com.example.vkr_todolist.domain.repository.NoteRepository

class DeleteNoteUseCase(private val noteRepository: NoteRepository) {
    suspend fun invoke(id: Int){
        noteRepository.deleteNote(id)
    }
}