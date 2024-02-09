package com.example.vkr_todolist.data.mapper

import com.example.vkr_todolist.data.model.ListEntity
import com.example.vkr_todolist.domain.model.ListModel

class ListEntityMapper : Mapper<ListEntity, ListModel>{
    override fun mapFromEntity(data: ListEntity): ListModel {
        return ListModel(
            id = data.id,
            title = data.title
        )
    }

    override fun mapToEntity(data: ListModel): ListEntity {
        return ListEntity(
            id = data.id,
            title = data.title
        )
    }

}