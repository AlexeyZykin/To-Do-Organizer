package com.example.vkr_todolist.data.source.local

import com.example.vkr_todolist.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TaskCacheDataSource {
    suspend fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun getTasksByList(listId: Int): Flow<List<TaskEntity>>
    suspend fun getTask(id: Int): TaskEntity
    suspend fun getImportantTasks(): Flow<List<TaskEntity>>
    suspend fun getCompletedTasks(): Flow<List<TaskEntity>>
    suspend fun searchTask(query: String): Flow<List<TaskEntity>>
    suspend fun getTodayTasks(today: Date): Flow<List<TaskEntity>>
    suspend fun getTomorrowTasks(tomorrow: Date): Flow<List<TaskEntity>>
    suspend fun getLaterTasks(tomorrowDate: Date): Flow<List<TaskEntity>>
    suspend fun getNoDatesTasks(): Flow<List<TaskEntity>>
    suspend fun deleteTasksByListId(listId: Int)
    suspend fun deleteCompletedTasks()
    suspend fun addTask(taskEntity: TaskEntity): Long
    suspend fun updateTask(taskEntity: TaskEntity)
    suspend fun deleteTask(id: Int)
}