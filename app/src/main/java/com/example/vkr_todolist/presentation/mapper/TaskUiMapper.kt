package com.example.vkr_todolist.presentation.mapper

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.presentation.model.TaskUi

class TaskUiMapper(private val listUiMapper: ListUiMapper) : Mapper<TaskUi, Task> {
    override fun mapFromUiModel(data: TaskUi): Task {
        return Task(
            id = data.id,
            title = data.title,
            description = data.description,
            checked = data.checked,
            list = listUiMapper.mapFromUiModel(data.list),
            date = data.date,
            createdDate = data.createdDate,
            isImportant = data.isImportant,
            reminder = data.reminder,
            imagePath = data.imagePath
        )
    }

    override fun mapToUiModel(data: Task): TaskUi {
        return TaskUi(
            id = data.id,
            title = data.title,
            description = data.description,
            checked = data.checked,
            list = listUiMapper.mapToUiModel(data.list),
            date = data.date,
            createdDate = data.createdDate,
            isImportant = data.isImportant,
            reminder = data.reminder,
            imagePath = data.imagePath
        )
    }
}