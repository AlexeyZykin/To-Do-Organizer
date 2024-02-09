package com.example.vkr_todolist.presentation.features.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkr_todolist.domain.usecase.note.AddNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.DeleteNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.GetAllNotesUseCase
import com.example.vkr_todolist.domain.usecase.note.GetNotesByListUseCase
import com.example.vkr_todolist.domain.usecase.note.SearchNoteUseCase
import com.example.vkr_todolist.presentation.mapper.NoteUiMapper
import com.example.vkr_todolist.presentation.model.NoteUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NoteViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getNotesByListUseCase: GetNotesByListUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val searchNoteUseCase: SearchNoteUseCase,
    private val noteUiMapper: NoteUiMapper
) : ViewModel() {

    private val _notes = MutableLiveData<List<NoteUi>>()
    val notes: LiveData<List<NoteUi>> get() = _notes

    fun getAllNotes() = viewModelScope.launch(Dispatchers.IO) {
        getAllNotesUseCase.invoke().distinctUntilChanged().collect {
            _notes.postValue(it.map { note -> noteUiMapper.mapToUiModel(note) })
        }
    }

    fun getNotesByList(listId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getNotesByListUseCase.invoke(listId).distinctUntilChanged().collect {
            _notes.postValue(it.map { note -> noteUiMapper.mapToUiModel(note) })
        }
    }

    fun addNote(noteUi: NoteUi) = viewModelScope.launch(Dispatchers.IO) {
        addNoteUseCase.invoke(noteUiMapper.mapFromUiModel(noteUi))
    }

    fun deleteNote(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteNoteUseCase.invoke(id)
    }

    fun searchNote(query: String) = viewModelScope.launch(Dispatchers.IO) {
        searchNoteUseCase.invoke("%$query%").distinctUntilChanged().collect {
            _notes.postValue(it.map { note -> noteUiMapper.mapToUiModel(note) })
        }
    }
}