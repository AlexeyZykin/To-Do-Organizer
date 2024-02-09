package com.example.vkr_todolist.presentation.model

import com.example.vkr_todolist.domain.model.ListModel

data class NoteUi(
    var id: Int?,
    var title: String,
    var description: String?,
    var time: String,
    var list: ListUi,
    var imagePath: String?
)
