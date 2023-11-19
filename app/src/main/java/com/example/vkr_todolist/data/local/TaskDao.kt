package com.example.vkr_todolist.data.local

import androidx.room.*
import com.example.vkr_todolist.data.model.Note
import com.example.vkr_todolist.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM task " +
            "ORDER BY CASE WHEN taskChecked = 0 THEN 0 ELSE 1 END, isImportant DESC, date ASC ")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE listId = :listId " +
            "ORDER by date ASC, CASE WHEN taskChecked = 0 THEN 0 ELSE 1 END, isImportant DESC")
    fun getTasksByList(listId: Int): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE isImportant = 1 " +
            "ORDER by date ASC, CASE WHEN taskChecked = 0 THEN 0 ELSE 1 END, isImportant DESC")
    fun getImportantTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE taskChecked = 1 " +
            "ORDER by isImportant DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE taskTitle LIKE :query ORDER by date ASC")
    fun searchTask(query: String): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date = :today " +
            "ORDER by CASE WHEN taskChecked = 0 THEN 0 ELSE 1 END, isImportant DESC")
    fun getTodayTasks(today: Date): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date = :tomorrow " +
            "ORDER by CASE WHEN taskChecked = 0 THEN 0 ELSE 1 END, isImportant DESC")
    fun getTomorrowTasks(tomorrow: Date): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date > :tomorrowDate " +
            "ORDER by date ASC, CASE WHEN taskChecked = 0 THEN 0 ELSE 1 END, isImportant DESC")
    fun getLaterTasks(tomorrowDate: Date): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date IS NULL " +
            "ORDER by CASE WHEN taskChecked = 0 THEN 0 ELSE 1 END, isImportant DESC")
    fun getNoDatesTasks(): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM task WHERE createdDate BETWEEN :startDate AND :endDate")
    fun getCreatedCountByPeriod(startDate: Date, endDate: Date): Flow<Int>

    @Query("SELECT COUNT(*) FROM task WHERE taskChecked = 1 AND createdDate BETWEEN :startDate AND :endDate")
    fun getCompletedCountByPeriod(startDate: Date, endDate: Date): Flow<Int>

    @Query("DELETE FROM task WHERE listId = :listId")
    suspend fun deleteTasksByListId(listId: Int)

    @Query("DELETE FROM task WHERE taskChecked = 1")
    suspend fun deleteCompletedTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Update
    suspend fun updateTasks(tasks: List<Task>)

    @Delete
    suspend fun deleteTask(task: Task)
}