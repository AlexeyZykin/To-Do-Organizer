package com.example.vkr_todolist.domain.model

import java.util.Date

data class Task(
    val id: Int?,
    var title: String,
    var description: String?,
    var checked: Boolean=false,
    var list: ListModel,
    var date: Date?,
    var createdDate: Date,
    var isImportant: Boolean=false,
    var reminder: Date?,
    var imagePath: String?
)