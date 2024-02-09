package com.example.vkr_todolist.data.repository

import com.example.vkr_todolist.data.source.local.ProductivityCacheDataSource
import com.example.vkr_todolist.domain.repository.ProductivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class ProductivityRepositoryImpl(private val productivityCacheDataSource: ProductivityCacheDataSource) :
    ProductivityRepository {
    override suspend fun getCreatedTasksCountByPeriod(startDate: Date, endDate: Date): Flow<Int> =
        flow {
            emit(
                productivityCacheDataSource.getCreatedTasksCountByPeriod(startDate, endDate)
            )
        }

    override suspend fun getCompletedTasksCountByPeriod(startDate: Date, endDate: Date): Flow<Int> =
        flow {
            emit(
                productivityCacheDataSource.getCompletedTasksCountByPeriod(startDate, endDate)
            )
        }
}