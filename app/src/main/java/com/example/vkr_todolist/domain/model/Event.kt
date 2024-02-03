package com.example.vkr_todolist.domain.model

import java.util.Date

data class Event(
    val id: Int?,
    var title: String,
    var list: List,
    var date: Date?,
    var reminder: Date?,
    var isFinished: Boolean = false,
)