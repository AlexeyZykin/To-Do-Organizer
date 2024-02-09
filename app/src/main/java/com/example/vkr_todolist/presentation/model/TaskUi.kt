package com.example.vkr_todolist.presentation.model

import com.example.vkr_todolist.domain.model.ListModel
import java.util.Date

data class TaskUi(
    var id: Int?,
    var title: String,
    var description: String?,
    var checked: Boolean=false,
    var list: ListUi,
    var date: Date?,
    var createdDate: Date,
    var isImportant: Boolean=false,
    var reminder: Date?,
    var imagePath: String?
)