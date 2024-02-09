package com.example.vkr_todolist.presentation.mapper

interface Mapper<UiModel, Model> {
    fun mapFromUiModel(data: UiModel): Model
    fun mapToUiModel(data: Model): UiModel
}