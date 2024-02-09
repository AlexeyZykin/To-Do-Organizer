package com.example.vkr_todolist.domain.repository

import com.example.vkr_todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TaskRepository {
    suspend fun getAllTasks(): Flow<List<Task>>
    suspend fun getTasksByList(listId: Int): Flow<List<Task>>
    suspend fun getTask(id: Int): Task
    suspend fun getImportantTasks(): Flow<List<Task>>
    suspend fun getCompletedTasks(): Flow<List<Task>>
    suspend fun searchTask(query: String): Flow<List<Task>>
    suspend fun getTodayTasks(today: Date): Flow<List<Task>>
    suspend fun getTomorrowTasks(tomorrow: Date): Flow<List<Task>>
    suspend fun getLaterTasks(tomorrowDate: Date): Flow<List<Task>>
    suspend fun getNoDatesTasks(): Flow<List<Task>>
    suspend fun deleteCompletedTasks()
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Int)
}