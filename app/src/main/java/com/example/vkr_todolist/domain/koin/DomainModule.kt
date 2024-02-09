package com.example.vkr_todolist.domain.koin

import com.example.vkr_todolist.data.repository.EventRepositoryImpl
import com.example.vkr_todolist.domain.usecase.event.AddEventUseCase
import com.example.vkr_todolist.domain.usecase.event.DeleteEndedEventsUseCase
import com.example.vkr_todolist.domain.usecase.event.DeleteEventUseCase
import com.example.vkr_todolist.domain.usecase.event.GetAllEventsFromListUseCase
import com.example.vkr_todolist.domain.usecase.event.GetAllEventsUseCase
import com.example.vkr_todolist.domain.usecase.event.GetEventUseCase
import com.example.vkr_todolist.domain.usecase.event.SearchEventUseCase
import com.example.vkr_todolist.domain.usecase.event.UpdateEventUseCase
import com.example.vkr_todolist.domain.usecase.list.AddListUseCase
import com.example.vkr_todolist.domain.usecase.list.DeleteListUseCase
import com.example.vkr_todolist.domain.usecase.list.GetAllListsUseCase
import com.example.vkr_todolist.domain.usecase.list.GetListUseCase
import com.example.vkr_todolist.domain.usecase.list.UpdateListUseCase
import com.example.vkr_todolist.domain.usecase.note.AddNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.DeleteNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.GetAllNotesUseCase
import com.example.vkr_todolist.domain.usecase.note.GetNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.GetNotesByListUseCase
import com.example.vkr_todolist.domain.usecase.note.SearchNoteUseCase
import com.example.vkr_todolist.domain.usecase.note.UpdateNoteUseCase
import com.example.vkr_todolist.domain.usecase.productivity.GetCompletedTasksCountUseCase
import com.example.vkr_todolist.domain.usecase.productivity.GetCreatedTasksCountUseCase
import com.example.vkr_todolist.domain.usecase.task.AddTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.DeleteCompletedTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.DeleteTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.GetAllTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetCompletedTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetImportantTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetLaterTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetNoDatesTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTasksByListUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTodayTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTomorrowTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.SearchTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.UpdateTaskUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { AddEventUseCase(get()) }
    factory { DeleteEndedEventsUseCase(get()) }
    factory { DeleteEventUseCase(get()) }
    factory { GetAllEventsFromListUseCase(get()) }
    factory { GetAllEventsUseCase(get()) }
    factory { GetEventUseCase(get()) }
    factory { SearchEventUseCase(get()) }
    factory { UpdateEventUseCase(get()) }

    factory { AddListUseCase(get()) }
    factory { DeleteListUseCase(get()) }
    factory { GetAllListsUseCase(get()) }
    factory { GetListUseCase(get()) }
    factory { UpdateListUseCase(get()) }

    factory { AddNoteUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { GetAllNotesUseCase(get()) }
    factory { GetNotesByListUseCase(get()) }
    factory { GetNoteUseCase(get()) }
    factory { SearchNoteUseCase(get()) }
    factory { UpdateNoteUseCase(get()) }

    factory { GetCompletedTasksCountUseCase(get()) }
    factory { GetCreatedTasksCountUseCase(get()) }

    factory { AddTaskUseCase(get()) }
    factory { DeleteCompletedTasksUseCase(get()) }
    factory { DeleteTaskUseCase(get()) }
    factory { GetAllTasksUseCase(get()) }
    factory { GetCompletedTasksUseCase(get()) }
    factory { GetImportantTasksUseCase(get()) }
    factory { GetLaterTasksUseCase(get()) }
    factory { GetNoDatesTasksUseCase(get()) }
    factory { GetTasksByListUseCase(get()) }
    factory { GetTaskUseCase(get()) }
    factory { GetTodayTasksUseCase(get()) }
    factory { GetTomorrowTasksUseCase(get()) }
    factory { SearchTaskUseCase(get()) }
    factory { UpdateTaskUseCase(get()) }
}