package com.example.vkr_todolist.cache.source

import com.example.vkr_todolist.cache.room.dao.EventDao
import com.example.vkr_todolist.cache.room.mapper.EventCacheMapper
import com.example.vkr_todolist.cache.room.model.EventCache
import com.example.vkr_todolist.data.model.EventEntity
import com.example.vkr_todolist.data.source.local.EventCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date


class EventCacheDataSourceImpl(
    private val eventDao: EventDao,
    private val eventCacheMapper: EventCacheMapper
) : EventCacheDataSource {
    override suspend fun getAllEvents(): Flow<List<EventEntity>> {
        return eventDao.getAllEvents()
            .map { it.map { event -> eventCacheMapper.mapFromCache(event) } }
    }

    override suspend fun getAllEventsFromList(listId: Int): Flow<List<EventEntity>> {
        return eventDao.getEventsByList(listId)
            .map { it.map { event -> eventCacheMapper.mapFromCache(event) } }
    }

    override suspend fun getEventById(id: Int): EventEntity {
        return eventCacheMapper.mapFromCache(eventDao.getEventById(id))
    }

    override suspend fun addEvent(event: EventEntity): Long {
        return eventDao.insertEvent(eventCacheMapper.mapToCache(event))
    }

    override suspend fun deleteEvent(id: Int) {
        eventDao.deleteEvent(id)
    }

    override suspend fun updateEvent(event: EventEntity) {
        eventDao.updateEvent(eventCacheMapper.mapToCache(event))
    }

    override suspend fun searchEvent(query: String): Flow<List<EventEntity>> {
        return eventDao.searchEvent(query)
            .map { it.map { event -> eventCacheMapper.mapFromCache(event) } }
    }

    override suspend fun deleteEndedEvents(currentDate: Date) {
        eventDao.deleteEndedEvents(currentDate)
    }

    override suspend fun deleteEventsByListId(listId: Int) {
        eventDao.deleteEventsByListId(listId)
    }
}