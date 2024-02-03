package com.example.vkr_todolist.domain.repository

interface NoteRepository {
    fun insertNote()
    fun deleteNote()
    fun updateNote()
    fun searchNote()
}