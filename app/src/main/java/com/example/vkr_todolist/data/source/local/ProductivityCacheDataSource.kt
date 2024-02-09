package com.example.vkr_todolist.data.source.local

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ProductivityCacheDataSource {
    suspend fun getCreatedTasksCountByPeriod(startDate: Date, endDate: Date): Int
    suspend fun getCompletedTasksCountByPeriod(startDate: Date, endDate: Date): Int
}