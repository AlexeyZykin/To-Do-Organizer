package com.example.vkr_todolist.presentation.features.note.addEditNote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkr_todolist.domain.usecase.list.GetAllListsUseCase
import com.example.vkr_todolist.domain.usecase.list.GetListUseCase
import com.example.vkr_todolist.domain.usecase.note.AddNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.GetNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.UpdateNoteUseCase
import com.example.vkr_todolist.presentation.mapper.ListUiMapper
import com.example.vkr_todolist.presentation.mapper.NoteUiMapper
import com.example.vkr_todolist.presentation.model.ListUi
import com.example.vkr_todolist.presentation.model.NoteUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class AddEditNoteViewModel(
    private val getNoteUseCase: GetNoteUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val getAllListsUseCase: GetAllListsUseCase,
    private val getListUseCase: GetListUseCase,
    private val noteUiMapper: NoteUiMapper,
    private val listUiMapper: ListUiMapper
) : ViewModel() {

    private val _note = MutableLiveData<NoteUi>()
    val note: LiveData<NoteUi> get() = _note
    private val _lists = MutableLiveData<List<ListUi>>()
    val lists: LiveData<List<ListUi>> get() = _lists
    private val _listItem = MutableLiveData<ListUi>()
    val listItem: LiveData<ListUi> get() = _listItem

    fun getNote(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _note.postValue(noteUiMapper.mapToUiModel(getNoteUseCase.invoke(id)))
    }

    fun getAllLists() = viewModelScope.launch(Dispatchers.IO) {
        getAllListsUseCase.invoke().distinctUntilChanged().collect { lists ->
            _lists.postValue(lists.map { listUiMapper.mapToUiModel(it) })
        }
    }

    fun updateNote(note: NoteUi) = viewModelScope.launch(Dispatchers.IO) {
        updateNoteUseCase.invoke(noteUiMapper.mapFromUiModel(note))
    }

    fun addNote(note: NoteUi) = viewModelScope.launch(Dispatchers.IO) {
        addNoteUseCase.invoke(noteUiMapper.mapFromUiModel(note))
    }

    fun getList(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        getListUseCase.invoke(id).distinctUntilChanged().collect {
            _listItem.postValue(listUiMapper.mapToUiModel(it))
        }
    }
}

