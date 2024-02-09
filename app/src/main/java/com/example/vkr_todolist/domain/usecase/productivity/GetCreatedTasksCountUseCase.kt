package com.example.vkr_todolist.domain.usecase.productivity

import com.example.vkr_todolist.domain.repository.ProductivityRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GetCreatedTasksCountUseCase(private val productivityRepository: ProductivityRepository) {
    suspend fun invoke(startDate: Date, endDate: Date): Flow<Int> {
        return productivityRepository.getCreatedTasksCountByPeriod(startDate, endDate)
    }
}