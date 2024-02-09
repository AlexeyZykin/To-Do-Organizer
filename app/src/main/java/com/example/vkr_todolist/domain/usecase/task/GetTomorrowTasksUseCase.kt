package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GetTomorrowTasksUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(tomorrow: Date): Flow<List<Task>> {
        return taskRepository.getTomorrowTasks(tomorrow)
    }
}