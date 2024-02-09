package com.example.vkr_todolist.presentation.features.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkr_todolist.cache.room.model.ListCache
import com.example.vkr_todolist.domain.model.ListModel
import com.example.vkr_todolist.domain.usecase.list.AddListUseCase
import com.example.vkr_todolist.domain.usecase.list.DeleteListUseCase
import com.example.vkr_todolist.domain.usecase.list.GetAllListsUseCase
import com.example.vkr_todolist.domain.usecase.list.GetListUseCase
import com.example.vkr_todolist.domain.usecase.list.UpdateListUseCase
import com.example.vkr_todolist.presentation.mapper.EventUiMapper
import com.example.vkr_todolist.presentation.mapper.ListUiMapper
import com.example.vkr_todolist.presentation.model.ListUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ListViewModel(
    private val getListUseCase: GetListUseCase,
    private val getAllListsUseCase: GetAllListsUseCase,
    private val addListUseCase: AddListUseCase,
    private val deleteListUseCase: DeleteListUseCase,
    private val updateListUseCase: UpdateListUseCase,
    private val listUiMapper: ListUiMapper
) : ViewModel() {

    private val _lists = MutableLiveData<List<ListUi>>()
    val lists: LiveData<List<ListUi>> get() = _lists
    private val _listItem = MutableLiveData<ListUi>()
    val listItem: LiveData<ListUi> get() = _listItem

    fun getAllLists() = viewModelScope.launch(Dispatchers.IO) {
        getAllListsUseCase.invoke().distinctUntilChanged().collect { lists ->
            _lists.postValue(lists.map { listUiMapper.mapToUiModel(it) })
        }
    }

    fun getListById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        getListUseCase.invoke(id).distinctUntilChanged().collect {
            _listItem.postValue(listUiMapper.mapToUiModel(it))
        }
    }

    fun addList(list: ListUi) = viewModelScope.launch(Dispatchers.IO) {
        addListUseCase.invoke(listUiMapper.mapFromUiModel(list))
    }

    fun updateList(list: ListUi) = viewModelScope.launch {
        updateListUseCase.invoke(listUiMapper.mapFromUiModel(list))
    }

    fun deleteList(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteListUseCase.invoke(id)
    }
}