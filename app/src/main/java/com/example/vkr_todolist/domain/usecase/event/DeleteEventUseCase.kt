package com.example.vkr_todolist.domain.usecase.event

import com.example.vkr_todolist.domain.repository.EventRepository

class DeleteEventUseCase(private val eventRepository: EventRepository) {
    suspend fun invoke(id: Int){
        eventRepository.deleteEvent(id)
    }
}