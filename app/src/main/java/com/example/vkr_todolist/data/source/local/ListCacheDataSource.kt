package com.example.vkr_todolist.data.source.local

import com.example.vkr_todolist.data.model.ListEntity
import com.example.vkr_todolist.domain.model.ListModel
import kotlinx.coroutines.flow.Flow

interface ListCacheDataSource {
    suspend fun getAllLists(): Flow<List<ListEntity>>
    suspend fun getListById(id: Int): ListEntity
    suspend fun deleteList(id: Int)
    suspend fun addList(list: ListEntity)
    suspend fun updateList(list: ListEntity)
}