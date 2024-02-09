package com.example.vkr_todolist.data.repository

import com.example.vkr_todolist.data.mapper.EventEntityMapper
import com.example.vkr_todolist.data.mapper.ListEntityMapper
import com.example.vkr_todolist.data.source.local.EventCacheDataSource
import com.example.vkr_todolist.data.source.local.ListCacheDataSource
import com.example.vkr_todolist.data.source.local.NoteCacheDataSource
import com.example.vkr_todolist.data.source.local.TaskCacheDataSource
import com.example.vkr_todolist.domain.model.ListModel
import com.example.vkr_todolist.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ListRepositoryImpl(
    private val listCacheDataSource: ListCacheDataSource,
    private val taskCacheDataSource: TaskCacheDataSource,
    private val noteCacheDataSource: NoteCacheDataSource,
    private val eventCacheDataSource: EventCacheDataSource,
    private val listEntityMapper: ListEntityMapper
) : ListRepository {
    override suspend fun getAllLists(): Flow<List<ListModel>> {
        return listCacheDataSource.getAllLists()
            .map { it.map { listItem -> listEntityMapper.mapFromEntity(listItem) } }
    }

    override suspend fun getListById(id: Int): Flow<ListModel> = flow {
        emit(
            listEntityMapper.mapFromEntity(listCacheDataSource.getListById(id))
        )
    }

    override suspend fun deleteList(id: Int) {
        listCacheDataSource.deleteList(id)
        taskCacheDataSource.deleteTasksByListId(id)
        noteCacheDataSource.deleteNotesByListId(id)
        eventCacheDataSource.deleteEventsByListId(id)
    }

    override suspend fun addList(list: ListModel) {
        listCacheDataSource.addList(listEntityMapper.mapToEntity(list))
    }

    override suspend fun updateList(list: ListModel) {
        listCacheDataSource.updateList(listEntityMapper.mapToEntity(list))
    }
}