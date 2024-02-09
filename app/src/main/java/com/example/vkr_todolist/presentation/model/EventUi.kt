package com.example.vkr_todolist.presentation.model

import com.example.vkr_todolist.domain.model.ListModel
import java.util.Date

data class EventUi(
    var id: Int?,
    var title: String,
    var list: ListUi,
    var date: Date,
    var reminder: Date?,
    var isFinished: Boolean = false,
)
