package com.example.vkr_todolist.cache.source

import com.example.vkr_todolist.cache.room.dao.TaskDao
import com.example.vkr_todolist.data.source.local.ProductivityCacheDataSource
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ProductivityCacheDataSourceImpl(private val taskDao: TaskDao) : ProductivityCacheDataSource {
    override suspend fun getCreatedTasksCountByPeriod(startDate: Date, endDate: Date): Int {
        return taskDao.getCreatedCountByPeriod(startDate, endDate)
    }

    override suspend fun getCompletedTasksCountByPeriod(startDate: Date, endDate: Date): Int {
        return taskDao.getCompletedCountByPeriod(startDate, endDate)
    }
}