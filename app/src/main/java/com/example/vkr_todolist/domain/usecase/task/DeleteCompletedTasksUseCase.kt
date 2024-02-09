package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.repository.TaskRepository

class DeleteCompletedTasksUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke() {
        taskRepository.deleteCompletedTasks()
    }
}