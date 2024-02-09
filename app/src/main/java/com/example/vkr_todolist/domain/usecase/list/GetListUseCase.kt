package com.example.vkr_todolist.domain.usecase.list

import com.example.vkr_todolist.domain.model.ListModel
import com.example.vkr_todolist.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow

class GetListUseCase(private val listRepository: ListRepository) {
    suspend fun invoke(id: Int): Flow<ListModel> = listRepository.getListById(id)
}