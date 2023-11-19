package com.example.vkr_todolist.ui.main

import androidx.lifecycle.*
import com.example.vkr_todolist.data.local.AppDatabase
import com.example.vkr_todolist.data.model.*
import com.example.vkr_todolist.utils.Constants
import com.example.vkr_todolist.utils.DateTimeManager
import kotlinx.coroutines.*

import java.util.*

class MainViewModel(database: AppDatabase): ViewModel() {
    private val daoNote = database.getNoteDao()
    private val daoTask = database.getTaskDao()
    private val daoListItem = database.getListDao()
    private val daoEvent = database.getEventDao()
    private val daoPomodoro = database.getPomodoroDao()


    val allNotes: LiveData<List<Note>> = daoNote.getAllNotes().asLiveData()
    val allListItem: LiveData<List<ListItem>> = daoListItem.getAllLists().asLiveData()
    val importantTasks: LiveData<List<Task>> = daoTask.getImportantTasks().asLiveData()
    val completedTasks: LiveData<List<Task>> = daoTask.getCompletedTasks().asLiveData()
    val allEvents: LiveData<List<Event>> = daoEvent.getAllEvents().asLiveData()
    val lastTimerState: LiveData<PomodoroTimer> = daoPomodoro.getPomodoroTimer()


    fun getAllNotesFromList(listId: Int): LiveData<List<Note>>{
        return daoNote.getNotesByList(listId).asLiveData()
    }

    fun getAllTasksFromList(listId: Int): LiveData<List<Task>>{
        return daoTask.getTasksByList(listId).asLiveData()
    }

    fun getAllEventsFromList(listId: Int): LiveData<List<Event>>{
        return daoEvent.getEventsByList(listId).asLiveData()
    }

    fun getListById(listId: Int): LiveData<List<ListItem>>{
        return daoListItem.getListById(listId).asLiveData()
    }


    private val selectedFilterTasks: MutableLiveData<Int> = MutableLiveData()
    private val tasksFiltered: LiveData<List<Task>> = selectedFilterTasks.switchMap { taskFilter ->
        when (taskFilter) {
            Constants.TASKS_ALL -> daoTask.getAllTasks().asLiveData()
            Constants.TASKS_TODAY -> daoTask.getTodayTasks(DateTimeManager.getCurrentDate()).asLiveData()
            Constants.TASKS_TOMORROW -> daoTask.getTomorrowTasks(DateTimeManager.getTomorrowDate()).asLiveData()
            Constants.TASKS_LATER -> daoTask.getLaterTasks(DateTimeManager.getTomorrowDate()).asLiveData()
            Constants.TASKS_NO_DATES -> daoTask.getNoDatesTasks().asLiveData()
            else -> throw IllegalArgumentException("Invalid selectedTaskFilter value")
        }
    }

    fun setSelectedFilter(selectedFilter: Int) {
        selectedFilterTasks.value = selectedFilter
    }

    fun getTasks(): LiveData<List<Task>> {
        return tasksFiltered
    }


    private val selectedTimeRange: MutableLiveData<Int> = MutableLiveData()
    private val createdTasksCount: LiveData<Int> = selectedTimeRange.switchMap { timeRange->
        when(timeRange){
            Constants.WEEK -> daoTask.getCreatedCountByPeriod(DateTimeManager.getFirstDayWeek(), DateTimeManager.getCurrentDate()).asLiveData()
            Constants.MONTH -> daoTask.getCreatedCountByPeriod(DateTimeManager.getFirstDayMonth(), DateTimeManager.getCurrentDate()).asLiveData()
            else -> throw IllegalArgumentException("Invalid selectedTimeRange value")
        }
    }

    private val completedTasksCount: LiveData<Int> = selectedTimeRange.switchMap { timeRange->
        when(timeRange){
            Constants.WEEK -> daoTask.getCompletedCountByPeriod(DateTimeManager.getFirstDayWeek(), DateTimeManager.getCurrentDate()).asLiveData()
            Constants.MONTH -> daoTask.getCompletedCountByPeriod(DateTimeManager.getFirstDayMonth(), DateTimeManager.getCurrentDate()).asLiveData()
            else -> throw IllegalArgumentException("Invalid selectedTimeRange value")
        }
    }

    fun getCreatedTasksCount(): LiveData<Int>{
        return createdTasksCount
    }

    fun getCompletedTasksCount(): LiveData<Int>{
        return completedTasksCount
    }

    fun setSelectedTimeRange(selectedItem: Int) {
        selectedTimeRange.value = selectedItem
    }


    //EVENT
    suspend fun insertEvent(event: Event): Deferred<Long> = viewModelScope.async(Dispatchers.IO){
        daoEvent.insertEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        daoEvent.deleteEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch{
        daoEvent.updateEvent(event)
    }

    fun searchEvent(query: String): LiveData<List<Event>>{
        return daoEvent.searchEvent("%$query%").asLiveData()
    }

    fun deleteEndedEvents(currentDate: Date) = viewModelScope.launch {
        daoEvent.deleteEndedEvents(currentDate)
    }

    //NOTE

    fun insertNote(note: Note)= viewModelScope.launch{
        daoNote.insertNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        daoNote.deleteNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch{
        daoNote.updateNote(note)
    }

    fun searchNote(query: String): LiveData<List<Note>>{
        return daoNote.searchNote("%$query%").asLiveData()
    }


    //TASK

    suspend fun insertTask(task: Task): Deferred<Long> = viewModelScope.async(Dispatchers.IO){
         daoTask.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch{
        daoTask.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        daoTask.deleteTask(task)
    }

    fun deleteCompletedTasks() = viewModelScope.launch {
        daoTask.deleteCompletedTasks()
    }

    fun searchTask(query: String): LiveData<List<Task>>{
        return daoTask.searchTask("%$query%").asLiveData()
    }

    //LIST
    fun insertListItem(list: ListItem) = viewModelScope.launch{
        daoListItem.insertListItem(list)
    }

    fun updateListItem(list: ListItem) = viewModelScope.launch{
        daoListItem.updateListItem(list)
    }

    fun deleteList(listId: Int) = viewModelScope.launch {
        daoListItem.deleteList(listId)
        daoTask.deleteTasksByListId(listId)
        daoNote.deleteNotesByListId(listId)
        daoEvent.deleteEventsByListId(listId)
    }

    //POMODORO
    fun insertTimerState(timer: PomodoroTimer) = viewModelScope.launch {
        val lastTimerState = daoPomodoro.getPomodoroTimer()
        if(lastTimerState.value == null)
            daoPomodoro.insertTimerState(timer)
        else
            daoPomodoro.updateTimerState(timer)
    }

    fun updateTimerState(timer: PomodoroTimer) = viewModelScope.launch {
        daoPomodoro.updateTimerState(timer)
    }



    class MainViewModelFactory(val database: AppDatabase):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw(java.lang.IllegalArgumentException("Unknown ViewModel class"))
        }
    }

}