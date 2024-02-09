package com.example.vkr_todolist.domain.usecase.event

import com.example.vkr_todolist.domain.model.Event
import com.example.vkr_todolist.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class GetAllEventsFromListUseCase(private val eventRepository: EventRepository) {
    suspend fun invoke(listId: Int): Flow<List<Event>> {
        return eventRepository.getAllEventsFromList(listId)
    }
}