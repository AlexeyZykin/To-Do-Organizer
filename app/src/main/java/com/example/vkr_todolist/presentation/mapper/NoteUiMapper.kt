package com.example.vkr_todolist.presentation.mapper

import com.example.vkr_todolist.domain.model.Note
import com.example.vkr_todolist.presentation.model.NoteUi

class NoteUiMapper(private val listUiMapper: ListUiMapper) : Mapper<NoteUi, Note>{
    override fun mapFromUiModel(data: NoteUi): Note {
        return Note(
            id = data.id,
            title = data.title,
            description = data.description,
            time = data.time,
            list = listUiMapper.mapFromUiModel(data.list),
            imagePath = data.imagePath
        )
    }

    override fun mapToUiModel(data: Note): NoteUi {
        return NoteUi(
            id = data.id,
            title = data.title,
            description = data.description,
            time = data.time,
            list = listUiMapper.mapToUiModel(data.list),
            imagePath = data.imagePath
        )
    }
}