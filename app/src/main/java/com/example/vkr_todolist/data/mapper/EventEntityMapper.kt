package com.example.vkr_todolist.data.mapper

import com.example.vkr_todolist.data.model.EventEntity
import com.example.vkr_todolist.domain.model.Event

class EventEntityMapper(private val listEntityMapper: ListEntityMapper) : Mapper<EventEntity, Event>{
    override fun mapFromEntity(data: EventEntity): Event {
        return Event(
            id = data.id,
            title = data.title,
            list = listEntityMapper.mapFromEntity(data.list),
            date = data.date,
            reminder = data.reminder,
            isFinished = data.isFinished
        )
    }

    override fun mapToEntity(data: Event): EventEntity {
        return EventEntity(
            id = data.id,
            title = data.title,
            list = listEntityMapper.mapToEntity(data.list),
            date = data.date,
            reminder = data.reminder,
            isFinished = data.isFinished
        )
    }
}