package com.example.vkr_todolist.domain.usecase.note

import com.example.vkr_todolist.domain.model.Note
import com.example.vkr_todolist.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesByListUseCase(private val noteRepository: NoteRepository) {
    suspend fun invoke(listId: Int): Flow<List<Note>> {
        return noteRepository.getNotesByList(listId)
    }
}