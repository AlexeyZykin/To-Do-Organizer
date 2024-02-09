package com.example.vkr_todolist.data.repository

import com.example.vkr_todolist.data.mapper.TaskEntityMapper
import com.example.vkr_todolist.data.source.local.TaskCacheDataSource
import com.example.vkr_todolist.domain.model.Task
import com.example.vkr_todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Date

class TaskRepositoryImpl(
    private val taskCacheDataSource: TaskCacheDataSource,
    private val taskEntityMapper: TaskEntityMapper
) : TaskRepository {
    override suspend fun getAllTasks(): Flow<List<Task>> {
        return taskCacheDataSource.getAllTasks()
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun getTasksByList(listId: Int): Flow<List<Task>> {
        return taskCacheDataSource.getTasksByList(listId)
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun getTask(id: Int): Task {
        return taskEntityMapper.mapFromEntity(taskCacheDataSource.getTask(id))
    }

    override suspend fun getImportantTasks(): Flow<List<Task>> {
        return taskCacheDataSource.getImportantTasks()
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun getCompletedTasks(): Flow<List<Task>> {
        return taskCacheDataSource.getCompletedTasks()
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun searchTask(query: String): Flow<List<Task>> {
        return taskCacheDataSource.searchTask(query)
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun getTodayTasks(today: Date): Flow<List<Task>> {
        return taskCacheDataSource.getTodayTasks(today)
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun getTomorrowTasks(tomorrow: Date): Flow<List<Task>> {
        return taskCacheDataSource.getTomorrowTasks(tomorrow)
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun getLaterTasks(tomorrowDate: Date): Flow<List<Task>> {
        return taskCacheDataSource.getLaterTasks(tomorrowDate)
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun getNoDatesTasks(): Flow<List<Task>> {
        return taskCacheDataSource.getNoDatesTasks()
            .map { it.map { task -> taskEntityMapper.mapFromEntity(task) } }
    }

    override suspend fun deleteCompletedTasks() {
        taskCacheDataSource.deleteCompletedTasks()
    }

    override suspend fun addTask(task: Task): Long {
        return taskCacheDataSource.addTask(taskEntityMapper.mapToEntity(task))
    }

    override suspend fun updateTask(task: Task) {
        taskCacheDataSource.addTask(taskEntityMapper.mapToEntity(task))
    }

    override suspend fun deleteTask(id: Int) {
        taskCacheDataSource.deleteTask(id)
    }
}