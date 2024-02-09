package com.example.vkr_todolist.domain.repository

import com.example.vkr_todolist.domain.model.ListModel
import kotlinx.coroutines.flow.Flow

interface ListRepository {
    suspend fun getAllLists(): Flow<List<ListModel>>
    suspend fun getListById(id: Int): Flow<ListModel>
    suspend fun deleteList(id: Int)
    suspend fun addList(list: ListModel)
    suspend fun updateList(list: ListModel)
}
