package com.example.vkr_todolist.domain.usecase.note

import com.example.vkr_todolist.domain.model.Note
import com.example.vkr_todolist.domain.repository.NoteRepository

class AddNoteUseCase(private val noteRepository: NoteRepository) {
    suspend fun invoke(note: Note){
        noteRepository.addNote(note)
    }
}