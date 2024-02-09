package com.example.vkr_todolist.presentation.features.productivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.vkr_todolist.domain.usecase.productivity.GetCompletedTasksCountUseCase
import com.example.vkr_todolist.domain.usecase.productivity.GetCreatedTasksCountUseCase
import com.example.vkr_todolist.domain.usecase.task.GetCompletedTasksUseCase
import com.example.vkr_todolist.presentation.utils.Constants
import com.example.vkr_todolist.presentation.utils.DateTimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ProductivityViewModel(
    private val getCompletedTasksCountUseCase: GetCompletedTasksCountUseCase,
    private val getCreatedTasksCountUseCase: GetCreatedTasksCountUseCase
) : ViewModel() {

    private val selectedTimeRange: MutableLiveData<Int> = MutableLiveData()
    private val _createdTasksCount: MediatorLiveData<Int> = MediatorLiveData()
    val createdTasksCount: LiveData<Int> get() = _createdTasksCount

    init {
        _createdTasksCount.addSource(selectedTimeRange) { timeRange ->
            viewModelScope.launch {
                when (timeRange) {
                    Constants.WEEK -> getCreatedTasksCountUseCase.invoke(
                        DateTimeManager.getFirstDayWeek(),
                        DateTimeManager.getCurrentDate()
                    ).distinctUntilChanged().collect {
                        _createdTasksCount.postValue(it)
                        Log.d("AndroidRuntime", it.toString())
                    }

                    Constants.MONTH -> getCreatedTasksCountUseCase.invoke(
                        DateTimeManager.getFirstDayMonth(),
                        DateTimeManager.getCurrentDate()
                    ).distinctUntilChanged().collect {
                        _createdTasksCount.postValue(it)
                    }

                    else -> throw IllegalArgumentException("Invalid selectedTimeRange value")
                }
            }
        }
    }

    private val _completedTasksCount: MediatorLiveData<Int> = MediatorLiveData()
    val completedTasksCount: LiveData<Int> get() = _completedTasksCount

    init {
        _completedTasksCount.addSource(selectedTimeRange) { timeRange ->
            viewModelScope.launch(Dispatchers.IO) {
                when (timeRange) {
                    Constants.WEEK -> {
                        getCompletedTasksCountUseCase.invoke(
                            DateTimeManager.getFirstDayWeek(),
                            DateTimeManager.getCurrentDate()
                        ).distinctUntilChanged().collect {
                            _completedTasksCount.postValue(it)
                            Log.d("AndroidRuntime", it.toString())
                        }
                    }

                    Constants.MONTH -> getCompletedTasksCountUseCase.invoke(
                        DateTimeManager.getFirstDayMonth(),
                        DateTimeManager.getCurrentDate()
                    ).distinctUntilChanged().collect {
                        _completedTasksCount.postValue(it)
                    }

                    else -> throw IllegalArgumentException("Invalid selectedTimeRange value")
                }
            }
        }
    }

    fun setSelectedTimeRange(selectedItem: Int) {
        selectedTimeRange.value = selectedItem

    }

}