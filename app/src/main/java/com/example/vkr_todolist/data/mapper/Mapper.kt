package com.example.vkr_todolist.data.mapper

interface Mapper<Entity, Model> {
    fun mapFromEntity(data: Entity): Model
    fun mapToEntity(data: Model): Entity
}