package com.example.vkr_todolist.presentation.features.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.vkr_todolist.domain.model.Event
import com.example.vkr_todolist.domain.usecase.event.AddEventUseCase
import com.example.vkr_todolist.domain.usecase.event.DeleteEndedEventsUseCase
import com.example.vkr_todolist.domain.usecase.event.DeleteEventUseCase
import com.example.vkr_todolist.domain.usecase.event.GetAllEventsFromListUseCase
import com.example.vkr_todolist.domain.usecase.event.GetAllEventsUseCase
import com.example.vkr_todolist.domain.usecase.event.SearchEventUseCase
import com.example.vkr_todolist.domain.usecase.event.UpdateEventUseCase
import com.example.vkr_todolist.presentation.mapper.EventUiMapper
import com.example.vkr_todolist.presentation.model.EventUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.Date

class EventViewModel(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val getAllEventsFromListUseCase: GetAllEventsFromListUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val searchEventUseCase: SearchEventUseCase,
    private val deleteEndedEventsUseCase: DeleteEndedEventsUseCase,
    private val eventUiMapper: EventUiMapper
) : ViewModel() {

    private val _events = MutableLiveData<List<EventUi>>()
    val events: LiveData<List<EventUi>> get() = _events

    fun getAllEvents() = viewModelScope.launch(Dispatchers.IO) {
        getAllEventsUseCase.invoke().distinctUntilChanged().collect {
            _events.postValue(it.map { event-> eventUiMapper.mapToUiModel(event) })
        }
    }

    fun addEvent(event: EventUi) = viewModelScope.launch(Dispatchers.IO) {
        addEventUseCase.invoke(eventUiMapper.mapFromUiModel(event))
    }

    fun deleteEvent(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteEventUseCase.invoke(id)
    }

    fun updateEvent(event: EventUi) = viewModelScope.launch {
        updateEventUseCase.invoke(eventUiMapper.mapFromUiModel(event))
    }

    fun searchEvent(query: String) = viewModelScope.launch(Dispatchers.IO) {
        searchEventUseCase.invoke("%$query%").distinctUntilChanged().collect {
            _events.postValue(it.map { event -> eventUiMapper.mapToUiModel(event)  })
        }
    }

    fun deleteEndedEvents(currentDate: Date) = viewModelScope.launch(Dispatchers.IO) {
        deleteEndedEventsUseCase.invoke(currentDate)
    }

    fun getAllEventsFromList(listId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getAllEventsFromListUseCase.invoke(listId).distinctUntilChanged().collect {
            _events.postValue(it.map { event -> eventUiMapper.mapToUiModel(event)  })
        }
    }
}