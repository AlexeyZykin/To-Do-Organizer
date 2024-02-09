package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository

class GetTaskUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(id: Int): Task {
        return taskRepository.getTask(id)
    }
}