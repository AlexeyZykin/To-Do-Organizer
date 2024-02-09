package com.example.vkr_todolist.domain.usecase.list

import com.example.vkr_todolist.domain.model.ListModel
import com.example.vkr_todolist.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow

class GetAllListsUseCase(private val listRepository: ListRepository) {
    suspend fun invoke(): Flow<List<ListModel>> {
        return listRepository.getAllLists()
    }
}