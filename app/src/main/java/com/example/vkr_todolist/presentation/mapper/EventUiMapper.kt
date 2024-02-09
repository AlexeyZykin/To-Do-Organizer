package com.example.vkr_todolist.presentation.mapper

import com.example.vkr_todolist.domain.model.Event
import com.example.vkr_todolist.presentation.model.EventUi

class EventUiMapper(
    private val listUiMapper: ListUiMapper
) : Mapper<EventUi, Event> {
    override fun mapFromUiModel(data: EventUi): Event {
        return Event(
            id = data.id,
            title = data.title,
            list = listUiMapper.mapFromUiModel(data.list),
            date = data.date,
            reminder = data.reminder,
            isFinished = data.isFinished
        )
    }

    override fun mapToUiModel(data: Event): EventUi {
        return EventUi(
            id = data.id,
            title = data.title,
            list = listUiMapper.mapToUiModel(data.list),
            date = data.date,
            reminder = data.reminder,
            isFinished = data.isFinished
        )
    }
}