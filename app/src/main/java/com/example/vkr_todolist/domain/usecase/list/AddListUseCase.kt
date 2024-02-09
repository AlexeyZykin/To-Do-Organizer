package com.example.vkr_todolist.domain.usecase.list

import com.example.vkr_todolist.domain.model.ListModel
import com.example.vkr_todolist.domain.repository.ListRepository

class AddListUseCase(private val listRepository: ListRepository) {
    suspend fun invoke(list: ListModel){
        listRepository.addList(list)
    }
}