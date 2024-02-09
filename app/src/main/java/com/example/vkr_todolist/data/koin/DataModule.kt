package com.example.vkr_todolist.data.koin

import com.example.vkr_todolist.data.mapper.EventEntityMapper
import com.example.vkr_todolist.data.mapper.ListEntityMapper
import com.example.vkr_todolist.data.mapper.NoteEntityMapper
import com.example.vkr_todolist.data.mapper.TaskEntityMapper
import com.example.vkr_todolist.data.repository.EventRepositoryImpl
import com.example.vkr_todolist.data.repository.ListRepositoryImpl
import com.example.vkr_todolist.data.repository.NoteRepositoryImpl
import com.example.vkr_todolist.data.repository.ProductivityRepositoryImpl
import com.example.vkr_todolist.data.repository.TaskRepositoryImpl
import com.example.vkr_todolist.domain.repository.EventRepository
import com.example.vkr_todolist.domain.repository.ListRepository
import com.example.vkr_todolist.domain.repository.NoteRepository
import com.example.vkr_todolist.domain.repository.ProductivityRepository
import com.example.vkr_todolist.domain.repository.TaskRepository
import org.koin.dsl.module

val dataModule = module {
    single<EventRepository> { EventRepositoryImpl(get(), get()) }
    single<ListRepository> { ListRepositoryImpl(get(), get(), get(), get(), get()) }
    single<NoteRepository> { NoteRepositoryImpl(get(), get()) }
    single<ProductivityRepository> { ProductivityRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }

    factory { EventEntityMapper(get()) }
    factory { ListEntityMapper() }
    factory { NoteEntityMapper(get()) }
    factory { TaskEntityMapper(get()) }
}