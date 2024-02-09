package com.example.vkr_todolist.cache.room.mapper

import com.example.vkr_todolist.cache.room.model.NoteCache
import com.example.vkr_todolist.data.model.NoteEntity

class NoteCacheMapper(private val listCacheMapper: ListCacheMapper) : Mapper<NoteCache, NoteEntity> {
    override fun mapFromCache(data: NoteCache): NoteEntity {
        return NoteEntity(
            id = data.id,
            title = data.title,
            description = data.description,
            time = data.time,
            list = listCacheMapper.mapFromCache(data.list),
            imagePath = data.imagePath
        )
    }

    override fun mapToCache(data: NoteEntity): NoteCache {
        return NoteCache(
            id = data.id,
            title = data.title,
            description = data.description,
            time = data.time,
            list = listCacheMapper.mapToCache(data.list),
            imagePath = data.imagePath
        )
    }

}