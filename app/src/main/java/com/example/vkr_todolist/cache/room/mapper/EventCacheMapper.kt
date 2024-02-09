package com.example.vkr_todolist.cache.room.mapper

import com.example.vkr_todolist.cache.room.model.EventCache
import com.example.vkr_todolist.data.model.EventEntity

class EventCacheMapper(private val listCacheMapper: ListCacheMapper) : Mapper<EventCache, EventEntity> {
    override fun mapFromCache(data: EventCache): EventEntity {
        return EventEntity(
            id = data.id,
            title = data.title,
            list = listCacheMapper.mapFromCache(data.list),
            date = data.date,
            reminder = data.reminder,
            isFinished = data.isFinished
        )
    }

    override fun mapToCache(data: EventEntity): EventCache {
        return EventCache(
            id = data.id,
            title = data.title,
            list = listCacheMapper.mapToCache(data.list),
            date = data.date,
            reminder = data.reminder,
            isFinished = data.isFinished
        )
    }
}