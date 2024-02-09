package com.example.vkr_todolist.domain.usecase.event

import com.example.vkr_todolist.domain.model.Event
import com.example.vkr_todolist.domain.repository.EventRepository

class GetEventUseCase(private val eventRepository: EventRepository) {
    suspend fun invoke(id: Int): Event {
        return eventRepository.getEvent(id)
    }
}