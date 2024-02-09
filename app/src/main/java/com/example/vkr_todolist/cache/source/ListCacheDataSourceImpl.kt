package com.example.vkr_todolist.cache.source

import com.example.vkr_todolist.cache.room.dao.ListDao
import com.example.vkr_todolist.cache.room.mapper.ListCacheMapper
import com.example.vkr_todolist.data.model.ListEntity
import com.example.vkr_todolist.data.source.local.ListCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ListCacheDataSourceImpl(
    private val listDao: ListDao,
    private val listCacheMapper: ListCacheMapper
    ) : ListCacheDataSource {
    override suspend fun getAllLists(): Flow<List<ListEntity>>  {
        return listDao.getAllLists().map { it.map { listItem -> listCacheMapper.mapFromCache(listItem) } }
    }

    override suspend fun getListById(id: Int): ListEntity {
        return listCacheMapper.mapFromCache(listDao.getListById(id))
    }

    override suspend fun deleteList(id: Int) {
        listDao.deleteList(id)
    }

    override suspend fun addList(list: ListEntity) {
        listDao.insertListItem(listCacheMapper.mapToCache(list))
    }

    override suspend fun updateList(list: ListEntity) {
        listDao.updateListItem(listCacheMapper.mapToCache(list))
    }
}