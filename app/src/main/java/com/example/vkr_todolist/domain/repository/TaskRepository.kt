package com.example.vkr_todolist.domain.repository

interface TaskRepository {
    fun insertTask()
    fun deleteTask()
    fun updateTask()
    fun deleteCompletedTasks()
    fun searchTask()
    fun getImportantTasks()
    fun getCompletedTasks()
}