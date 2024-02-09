package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class SearchTaskUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(query: String): Flow<List<Task>> {
        return taskRepository.searchTask(query)
    }
}