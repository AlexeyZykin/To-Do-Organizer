package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GetLaterTasksUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(tomorrowDate: Date): Flow<List<Task>> {
        return taskRepository.getLaterTasks(tomorrowDate)
    }
}