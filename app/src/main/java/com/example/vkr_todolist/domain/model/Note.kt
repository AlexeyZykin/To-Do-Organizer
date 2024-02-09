package com.example.vkr_todolist.domain.model

data class Note(
    val id: Int?,
    var title: String,
    var description: String?,
    var time: String,
    var list: ListModel,
    var imagePath: String?
)