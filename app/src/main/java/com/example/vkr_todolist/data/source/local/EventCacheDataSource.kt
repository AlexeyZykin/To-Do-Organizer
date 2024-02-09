package com.example.vkr_todolist.data.source.local

import com.example.vkr_todolist.data.model.EventEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date


interface EventCacheDataSource {
    suspend fun getAllEvents(): Flow<List<EventEntity>>
    suspend fun getAllEventsFromList(listId: Int): Flow<List<EventEntity>>
    suspend fun getEventById(id: Int): EventEntity
    suspend fun addEvent(event: EventEntity): Long
    suspend fun deleteEvent(id: Int)
    suspend fun updateEvent(event: EventEntity)
    suspend fun searchEvent(query: String): Flow<List<EventEntity>>
    suspend fun deleteEndedEvents(currentDate: Date)
    suspend fun deleteEventsByListId(listId: Int)
}