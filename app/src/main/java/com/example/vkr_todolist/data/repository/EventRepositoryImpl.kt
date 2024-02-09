package com.example.vkr_todolist.data.repository

import com.example.vkr_todolist.data.mapper.EventEntityMapper
import com.example.vkr_todolist.data.model.EventEntity
import com.example.vkr_todolist.data.source.local.EventCacheDataSource
import com.example.vkr_todolist.domain.model.Event
import com.example.vkr_todolist.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Date

class EventRepositoryImpl(
    private val eventCacheDataSource: EventCacheDataSource,
    private val eventEntityMapper: EventEntityMapper
    ) : EventRepository{
    override suspend fun getAllEvents(): Flow<List<Event>> {
        return eventCacheDataSource.getAllEvents()
            .map { it.map { event -> eventEntityMapper.mapFromEntity(event) } }

    }

    override suspend fun getAllEventsFromList(listId: Int): Flow<List<Event>> {
        return eventCacheDataSource.getAllEventsFromList(listId)
            .map { it.map { event -> eventEntityMapper.mapFromEntity(event) } }
    }

    override suspend fun getEvent(id: Int): Event {
        return eventEntityMapper.mapFromEntity(eventCacheDataSource.getEventById(id))
    }

    override suspend fun addEvent(event: Event): Long {
        return eventCacheDataSource.addEvent(eventEntityMapper.mapToEntity(event))
    }

    override suspend fun deleteEvent(id: Int) {
        eventCacheDataSource.deleteEvent(id)
    }

    override suspend fun updateEvent(event: Event) {
        eventCacheDataSource.updateEvent(eventEntityMapper.mapToEntity(event))
    }

    override suspend fun searchEvent(query: String): Flow<List<Event>> {
        return eventCacheDataSource.searchEvent(query)
            .map { it.map { event -> eventEntityMapper.mapFromEntity(event) } }
    }

    override suspend fun deleteEndedEvents(currentDate: Date) {
        eventCacheDataSource.deleteEndedEvents(currentDate)
    }
}