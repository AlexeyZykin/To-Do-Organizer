package com.example.vkr_todolist.data.model

import com.example.vkr_todolist.domain.model.ListModel

data class NoteEntity(
    val id: Int?,
    var title: String,
    var description: String?,
    var time: String,
    var list: ListEntity,
    var imagePath: String?
)
