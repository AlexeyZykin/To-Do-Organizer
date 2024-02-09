package com.example.vkr_todolist.presentation.koin

import com.example.vkr_todolist.presentation.features.event.EventViewModel
import com.example.vkr_todolist.presentation.features.event.addEditEvent.AddEditEventViewModel
import com.example.vkr_todolist.presentation.features.list.ListViewModel
import com.example.vkr_todolist.presentation.features.note.NoteViewModel
import com.example.vkr_todolist.presentation.features.note.addEditNote.AddEditNoteViewModel
import com.example.vkr_todolist.presentation.features.productivity.ProductivityViewModel
import com.example.vkr_todolist.presentation.features.task.AddEditTaskViewModel
import com.example.vkr_todolist.presentation.features.task.TaskViewModel
import com.example.vkr_todolist.presentation.mapper.EventUiMapper
import com.example.vkr_todolist.presentation.mapper.ListUiMapper
import com.example.vkr_todolist.presentation.mapper.NoteUiMapper
import com.example.vkr_todolist.presentation.mapper.TaskUiMapper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { EventViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ListViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { AddEditEventViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { NoteViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { AddEditNoteViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { TaskViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddEditTaskViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ProductivityViewModel(get(), get()) }

    factory { EventUiMapper(get()) }
    factory { NoteUiMapper(get()) }
    factory { TaskUiMapper(get()) }
    factory { ListUiMapper() }
}