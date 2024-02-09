package com.example.vkr_todolist.domain.usecase.event

import com.example.vkr_todolist.domain.model.Event
import com.example.vkr_todolist.domain.repository.EventRepository

class UpdateEventUseCase(private val eventRepository: EventRepository) {
    suspend fun invoke(event: Event){
        eventRepository.updateEvent(event)
    }
}