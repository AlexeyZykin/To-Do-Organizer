package com.example.vkr_todolist.presentation.features.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkr_todolist.domain.usecase.list.GetAllListsUseCase
import com.example.vkr_todolist.domain.usecase.list.GetListUseCase
import com.example.vkr_todolist.domain.usecase.task.AddTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.UpdateTaskUseCase
import com.example.vkr_todolist.presentation.mapper.ListUiMapper
import com.example.vkr_todolist.presentation.mapper.TaskUiMapper
import com.example.vkr_todolist.presentation.model.ListUi
import com.example.vkr_todolist.presentation.model.TaskUi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class AddEditTaskViewModel(
    private val getTaskUseCase: GetTaskUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getAllListsUseCase: GetAllListsUseCase,
    private val getListUseCase: GetListUseCase,
    private val taskUiMapper: TaskUiMapper,
    private val listUiMapper: ListUiMapper
) : ViewModel() {

    private val _task = MutableLiveData<TaskUi>()
    val task: LiveData<TaskUi> get() = _task
    private val _lists = MutableLiveData<List<ListUi>>()
    val lists: LiveData<List<ListUi>> get() = _lists
    private val _listItem = MutableLiveData<ListUi>()
    val listItem: LiveData<ListUi> get() = _listItem

    fun addTask(task: TaskUi): Deferred<Long> = viewModelScope.async(Dispatchers.IO) {
        addTaskUseCase.invoke(taskUiMapper.mapFromUiModel(task))
    }

    fun getTask(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _task.postValue(taskUiMapper.mapToUiModel(getTaskUseCase.invoke(id)))
    }

    fun getAllLists() = viewModelScope.launch(Dispatchers.IO) {
        getAllListsUseCase.invoke().distinctUntilChanged().collect { lists ->
            _lists.postValue(lists.map { listUiMapper.mapToUiModel(it) })
        }
    }

    fun updateTask(taskUi: TaskUi) = viewModelScope.launch(Dispatchers.IO) {
        updateTaskUseCase.invoke(taskUiMapper.mapFromUiModel(taskUi))
    }

    fun getList(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        getListUseCase.invoke(id).distinctUntilChanged().collect {
            _listItem.postValue(listUiMapper.mapToUiModel(it))
        }
    }
}