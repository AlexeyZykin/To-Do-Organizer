package com.example.vkr_todolist.presentation.features.event.addEditEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkr_todolist.domain.usecase.event.AddEventUseCase
import com.example.vkr_todolist.domain.usecase.event.GetEventUseCase
import com.example.vkr_todolist.domain.usecase.event.UpdateEventUseCase
import com.example.vkr_todolist.domain.usecase.list.GetAllListsUseCase
import com.example.vkr_todolist.domain.usecase.list.GetListUseCase
import com.example.vkr_todolist.presentation.mapper.EventUiMapper
import com.example.vkr_todolist.presentation.mapper.ListUiMapper
import com.example.vkr_todolist.presentation.model.EventUi
import com.example.vkr_todolist.presentation.model.ListUi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class AddEditEventViewModel(
    private val getEventUseCase: GetEventUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getAllListsUseCase: GetAllListsUseCase,
    private val getListUseCase: GetListUseCase,
    private val eventUiMapper: EventUiMapper,
    private val listUiMapper: ListUiMapper
) : ViewModel() {

    private val _event = MutableLiveData<EventUi>()
    val event: LiveData<EventUi> get() = _event
    private val _lists = MutableLiveData<List<ListUi>>()
    val lists: LiveData<List<ListUi>> get() = _lists
    private val _listItem = MutableLiveData<ListUi>()
    val listItem: LiveData<ListUi> get() = _listItem

    fun addEvent(event: EventUi) : Deferred<Long> = viewModelScope.async(Dispatchers.IO) {
        addEventUseCase.invoke(eventUiMapper.mapFromUiModel(event))
    }

    fun getEvent(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _event.postValue(eventUiMapper.mapToUiModel(getEventUseCase.invoke(id)))
    }

    fun updateEvent(event: EventUi) =viewModelScope.launch(Dispatchers.IO) {
        updateEventUseCase.invoke(eventUiMapper.mapFromUiModel(event))
    }

    fun getAllLists() = viewModelScope.launch(Dispatchers.IO) {
        getAllListsUseCase.invoke().distinctUntilChanged().collect { lists ->
            _lists.postValue(lists.map { listUiMapper.mapToUiModel(it) })
        }
    }

    fun getList(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        getListUseCase.invoke(id).distinctUntilChanged().collect {
            _listItem.postValue(listUiMapper.mapToUiModel(it))
        }
    }
}