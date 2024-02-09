package com.example.vkr_todolist.cache.room.mapper

import com.example.vkr_todolist.cache.room.model.ListCache
import com.example.vkr_todolist.data.model.ListEntity

class ListCacheMapper : Mapper<ListCache, ListEntity> {
    override fun mapFromCache(data: ListCache): ListEntity {
        return ListEntity(
            id = data.listId,
            title = data.title
        )
    }

    override fun mapToCache(data: ListEntity): ListCache {
        return ListCache(
            listId = data.id,
            title = data.title
        )
    }
}