package com.example.vkr_todolist.cache.source

import com.example.vkr_todolist.cache.room.dao.TaskDao
import com.example.vkr_todolist.cache.room.mapper.TaskCacheMapper
import com.example.vkr_todolist.data.model.TaskEntity
import com.example.vkr_todolist.data.source.local.TaskCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class TaskCacheDataSourceImpl(
    private val taskDao: TaskDao,
    private val taskCacheMapper: TaskCacheMapper
) : TaskCacheDataSource {
    override suspend fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks().map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun getTasksByList(listId: Int): Flow<List<TaskEntity>> {
        return taskDao.getTasksByList(listId)
            .map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun getTask(id: Int): TaskEntity {
        return taskCacheMapper.mapFromCache(taskDao.getTaskById(id))
    }

    override suspend fun getImportantTasks(): Flow<List<TaskEntity>> {
        return taskDao.getImportantTasks()
            .map { it.map { task-> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun getCompletedTasks(): Flow<List<TaskEntity>> {
        return taskDao.getCompletedTasks()
            .map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun searchTask(query: String): Flow<List<TaskEntity>> {
        return taskDao.searchTask(query)
            .map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun getTodayTasks(today: Date): Flow<List<TaskEntity>> {
        return taskDao.getTodayTasks(today)
            .map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun getTomorrowTasks(tomorrow: Date): Flow<List<TaskEntity>> {
        return taskDao.getTomorrowTasks(tomorrow)
            .map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun getLaterTasks(tomorrowDate: Date): Flow<List<TaskEntity>> {
        return taskDao.getLaterTasks(tomorrowDate)
            .map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun getNoDatesTasks(): Flow<List<TaskEntity>> {
        return taskDao.getNoDatesTasks()
            .map { it.map { task -> taskCacheMapper.mapFromCache(task) } }
    }

    override suspend fun deleteTasksByListId(listId: Int) {
        taskDao.deleteTasksByListId(listId)
    }

    override suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    override suspend fun addTask(taskEntity: TaskEntity): Long {
        return taskDao.insertTask(taskCacheMapper.mapToCache(taskEntity))
    }

    override suspend fun updateTask(taskEntity: TaskEntity) {
        taskDao.updateTask(taskCacheMapper.mapToCache(taskEntity))
    }

    override suspend fun deleteTask(id: Int) {
        taskDao.deleteTask(id)
    }
}