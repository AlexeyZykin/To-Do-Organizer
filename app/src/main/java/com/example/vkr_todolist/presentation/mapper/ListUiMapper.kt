package com.example.vkr_todolist.presentation.mapper

import com.example.vkr_todolist.domain.model.ListModel
import com.example.vkr_todolist.presentation.model.ListUi

class ListUiMapper : Mapper<ListUi, ListModel> {
    override fun mapFromUiModel(data: ListUi): ListModel {
        return ListModel(
            id = data.id,
            title = data.title
        )
    }

    override fun mapToUiModel(data: ListModel): ListUi {
        return ListUi(
            id = data.id,
            title = data.title
        )
    }
}