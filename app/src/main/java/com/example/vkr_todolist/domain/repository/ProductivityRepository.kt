package com.example.vkr_todolist.domain.repository

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ProductivityRepository {
    suspend fun getCreatedTasksCountByPeriod(startDate: Date, endDate: Date): Flow<Int>
    suspend fun getCompletedTasksCountByPeriod(startDate: Date, endDate: Date): Flow<Int>
}