package com.example.vkr_todolist.cache.room.mapper

import com.example.vkr_todolist.cache.room.model.TaskCache
import com.example.vkr_todolist.data.model.TaskEntity
import com.example.vkr_todolist.presentation.mapper.ListUiMapper

class TaskCacheMapper(private val listCacheMapper: ListCacheMapper) : Mapper<TaskCache, TaskEntity>{
    override fun mapFromCache(data: TaskCache): TaskEntity {
        return TaskEntity(
            id = data.id,
            title = data.title,
            description = data.description,
            checked = data.checked,
            list = listCacheMapper.mapFromCache(data.list),
            date = data.date,
            createdDate = data.createdDate,
            isImportant = data.isImportant,
            reminder = data.reminder,
            imagePath = data.imagePath
        )
    }

    override fun mapToCache(data: TaskEntity): TaskCache {
        return TaskCache(
            id = data.id,
            title = data.title,
            description = data.description,
            checked = data.checked,
            list = listCacheMapper.mapToCache(data.list),
            date = data.date,
            createdDate = data.createdDate,
            isImportant = data.isImportant,
            reminder = data.reminder,
            imagePath = data.imagePath
        )
    }
}