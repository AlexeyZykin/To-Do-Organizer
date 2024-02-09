package com.example.vkr_todolist.cache.room.mapper

interface Mapper<Cache, Entity> {
    fun mapFromCache(data: Cache): Entity
    fun mapToCache(data: Entity): Cache
}