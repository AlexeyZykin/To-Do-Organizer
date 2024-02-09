package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.repository.TaskRepository

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(id: Int) {
        taskRepository.deleteTask(id)
    }
}