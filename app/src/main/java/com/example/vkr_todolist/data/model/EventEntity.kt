package com.example.vkr_todolist.data.model

import java.util.Date

data class EventEntity(
    val id: Int?,
    var title: String,
    var list: ListEntity,
    var date: Date,
    var reminder: Date?,
    var isFinished: Boolean = false,
)