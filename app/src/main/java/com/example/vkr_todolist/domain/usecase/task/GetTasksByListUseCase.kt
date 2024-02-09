package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksByListUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(listId: Int): Flow<List<Task>> {
        return taskRepository.getTasksByList(listId)
    }
}