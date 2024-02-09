package com.example.vkr_todolist.domain.usecase.event

import com.example.vkr_todolist.domain.repository.EventRepository
import java.util.Date

class DeleteEndedEventsUseCase(private val eventRepository: EventRepository) {
    suspend fun invoke(currentDate: Date) {
        eventRepository.deleteEndedEvents(currentDate)
    }
}