package com.example.vkr_todolist.data.mapper

import com.example.vkr_todolist.data.model.TaskEntity
import com.example.vkr_todolist.domain.model.Task

class TaskEntityMapper(private val listEntityMapper: ListEntityMapper) : Mapper<TaskEntity, Task> {
    override fun mapFromEntity(data: TaskEntity): Task {
        return Task(
            id = data.id,
            title = data.title,
            description = data.description,
            checked = data.checked,
            list = listEntityMapper.mapFromEntity(data.list),
            date = data.date,
            createdDate = data.createdDate,
            isImportant = data.isImportant,
            reminder = data.reminder,
            imagePath = data.imagePath
        )
    }

    override fun mapToEntity(data: Task): TaskEntity {
        return TaskEntity(
            id = data.id,
            title = data.title,
            description = data.description,
            checked = data.checked,
            list = listEntityMapper.mapToEntity(data.list),
            date = data.date,
            createdDate = data.createdDate,
            isImportant = data.isImportant,
            reminder = data.reminder,
            imagePath = data.imagePath
        )
    }
}