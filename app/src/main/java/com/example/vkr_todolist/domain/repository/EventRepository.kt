package com.example.vkr_todolist.domain.repository

import com.example.vkr_todolist.data.model.EventEntity
import com.example.vkr_todolist.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface EventRepository {
    suspend fun getAllEvents(): Flow<List<Event>>
    suspend fun getAllEventsFromList(listId: Int): Flow<List<Event>>
    suspend fun getEvent(id: Int): Event
    suspend fun addEvent(event: Event): Long
    suspend fun deleteEvent(id: Int)
    suspend fun updateEvent(event: Event)
    suspend fun searchEvent(query: String): Flow<List<Event>>
    suspend fun deleteEndedEvents(currentDate: Date)
}