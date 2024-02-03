package com.example.vkr_todolist.domain.repository

import com.example.vkr_todolist.domain.model.Event

interface EventRepository {
    fun getAllEventsFromList(): List<Event>
    fun addEvent(event: Event)
    fun deleteEvent(event: Event)
    fun updateEvent()
    fun searchEvent()
    fun deleteEndedEvents()
}