package com.example.vkr_todolist.data.model

import com.example.vkr_todolist.domain.model.ListModel
import java.util.Date

data class TaskEntity(
    val id: Int?,
    var title: String,
    var description: String?,
    var checked: Boolean=false,
    var list: ListEntity,
    var date: Date?,
    var createdDate: Date,
    var isImportant: Boolean=false,
    var reminder: Date?,
    var imagePath: String?
)