package com.example.vkr_todolist.domain.usecase.event

import com.example.vkr_todolist.domain.model.Event
import com.example.vkr_todolist.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class SearchEventUseCase(private val eventRepository: EventRepository) {
    suspend fun invoke(query: String): Flow<List<Event>> {
        return eventRepository.searchEvent(query)
    }
}