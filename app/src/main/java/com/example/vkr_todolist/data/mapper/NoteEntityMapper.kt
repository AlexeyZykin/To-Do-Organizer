package com.example.vkr_todolist.data.mapper

import com.example.vkr_todolist.data.model.NoteEntity
import com.example.vkr_todolist.domain.model.Note

class NoteEntityMapper(private val listEntityMapper: ListEntityMapper) : Mapper<NoteEntity, Note>{
    override fun mapFromEntity(data: NoteEntity): Note {
        return Note(
            id = data.id,
            title = data.title,
            description = data.description,
            time = data.time,
            list = listEntityMapper.mapFromEntity(data.list),
            imagePath = data.imagePath
        )
    }

    override fun mapToEntity(data: Note): NoteEntity {
        return NoteEntity(
            id = data.id,
            title = data.title,
            description = data.description,
            time = data.time,
            list = listEntityMapper.mapToEntity(data.list),
            imagePath = data.imagePath
        )
    }
}