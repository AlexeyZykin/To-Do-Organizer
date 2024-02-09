package com.example.vkr_todolist.presentation.features.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.vkr_todolist.cache.room.model.TaskCache
import com.example.vkr_todolist.domain.usecase.task.AddTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.DeleteCompletedTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.DeleteTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.GetAllTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetCompletedTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetImportantTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetLaterTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetNoDatesTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTasksByListUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTodayTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.GetTomorrowTasksUseCase
import com.example.vkr_todolist.domain.usecase.task.SearchTaskUseCase
import com.example.vkr_todolist.domain.usecase.task.UpdateTaskUseCase
import com.example.vkr_todolist.presentation.mapper.TaskUiMapper
import com.example.vkr_todolist.presentation.model.TaskUi
import com.example.vkr_todolist.presentation.utils.Constants
import com.example.vkr_todolist.presentation.utils.DateTimeManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getTasksByListUseCase: GetTasksByListUseCase,
    private val getImportantTasksUseCase: GetImportantTasksUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteCompletedTasksUseCase: DeleteCompletedTasksUseCase,
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val getTomorrowTasksUseCase: GetTomorrowTasksUseCase,
    private val getLaterTasksUseCase: GetLaterTasksUseCase,
    private val getNoDatesTasksUseCase: GetNoDatesTasksUseCase,
    private val searchTaskUseCase: SearchTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val taskUiMapper: TaskUiMapper
) : ViewModel() {

    private val _tasks = MutableLiveData<List<TaskUi>>()
    val tasks: LiveData<List<TaskUi>> get() = _tasks
    private val selectedFilterTasks: MutableLiveData<Int> = MutableLiveData()
    private val _tasksFiltered: MediatorLiveData<List<TaskUi>> = MediatorLiveData()
    val tasksFiltered: LiveData<List<TaskUi>> get() = _tasksFiltered

    init {
        _tasksFiltered.addSource(selectedFilterTasks) { filter ->
            viewModelScope.launch(Dispatchers.IO) {
                when (filter) {
                    Constants.TASKS_ALL ->
                        getAllTasksUseCase.invoke()
                            .distinctUntilChanged()
                            .collect {
                                _tasksFiltered.postValue(it.map { task ->
                                    taskUiMapper.mapToUiModel(task)
                                })
                            }

                    Constants.TASKS_TODAY ->
                        getTodayTasksUseCase.invoke(DateTimeManager.getCurrentDate())
                            .distinctUntilChanged()
                            .collect {
                                _tasksFiltered.postValue(it.map { task ->
                                    taskUiMapper.mapToUiModel(task)
                                })
                            }

                    Constants.TASKS_TOMORROW ->
                        getTomorrowTasksUseCase.invoke(DateTimeManager.getTomorrowDate())
                            .distinctUntilChanged()
                            .collect {
                                _tasksFiltered.postValue(it.map { task ->
                                    taskUiMapper.mapToUiModel(task)
                                })
                            }

                    Constants.TASKS_LATER ->
                        getLaterTasksUseCase.invoke(DateTimeManager.getTomorrowDate())
                            .distinctUntilChanged()
                            .collect {
                                _tasksFiltered.postValue(it.map { task ->
                                    taskUiMapper.mapToUiModel(task)
                                })
                            }

                    Constants.TASKS_NO_DATES ->
                        getNoDatesTasksUseCase.invoke()
                            .distinctUntilChanged()
                            .collect {
                                _tasksFiltered.postValue(it.map { task ->
                                    taskUiMapper.mapToUiModel(task)
                                })
                            }

                    else -> throw IllegalArgumentException("Invalid selectedTaskFilter value")
                }
            }
        }
    }

    fun setSelectedFilter(selectedFilter: Int) {
        selectedFilterTasks.value = selectedFilter
    }

    fun addTask(task: TaskUi) = viewModelScope.async(Dispatchers.IO) {
        addTaskUseCase.invoke(taskUiMapper.mapFromUiModel(task))
    }

    fun deleteTask(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteTaskUseCase.invoke(id)
    }

    fun deleteCompletedTasks() = viewModelScope.launch {
        deleteCompletedTasksUseCase.invoke()
    }

    fun updateTask(task: TaskUi) = viewModelScope.launch(Dispatchers.IO) {
        updateTaskUseCase.invoke(taskUiMapper.mapFromUiModel(task))
    }

    fun getAllTasksByList(listId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getTasksByListUseCase.invoke(listId).distinctUntilChanged().collect {
            _tasks.postValue(it.map { task -> taskUiMapper.mapToUiModel(task) })
        }
    }

    fun getCompletedTasks() = viewModelScope.launch(Dispatchers.IO) {
        getCompletedTasksUseCase.invoke().distinctUntilChanged().collect {
            _tasks.postValue(it.map { task -> taskUiMapper.mapToUiModel(task) })
        }
    }

    fun getImportantTasks() = viewModelScope.launch(Dispatchers.IO) {
        getImportantTasksUseCase.invoke().distinctUntilChanged().collect {
            _tasks.postValue(it.map { task -> taskUiMapper.mapToUiModel(task) })
        }
    }

    fun searchTask(query: String) = viewModelScope.launch(Dispatchers.IO) {
        searchTaskUseCase.invoke("%$query%").distinctUntilChanged().collect {
            _tasks.postValue(it.map { task -> taskUiMapper.mapToUiModel(task) })
        }
    }
}