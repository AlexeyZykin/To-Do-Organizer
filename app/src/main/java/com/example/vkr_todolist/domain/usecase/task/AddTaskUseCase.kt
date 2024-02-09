package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository

class AddTaskUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(task: Task): Long {
        return taskRepository.addTask(task)
    }
}