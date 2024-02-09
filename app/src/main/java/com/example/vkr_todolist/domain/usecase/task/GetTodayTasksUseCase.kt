package com.example.vkr_todolist.domain.usecase.task

import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GetTodayTasksUseCase(private val taskRepository: TaskRepository) {
    suspend fun invoke(today: Date): Flow<List<Task>> {
        return taskRepository.getTodayTasks(today)
    }
}