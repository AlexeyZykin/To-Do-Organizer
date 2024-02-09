package com.example.vkr_todolist.domain.usecase.list

import com.example.vkr_todolist.domain.repository.ListRepository

class DeleteListUseCase(private val listRepository: ListRepository) {
    suspend fun invoke(id: Int){
        listRepository.deleteList(id)
    }
}