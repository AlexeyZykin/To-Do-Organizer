package com.example.vkr_todolist.cache.room.dao

import androidx.room.*
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import com.example.vkr_todolist.cache.room.model.TaskCache
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TaskDao {
    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} " +
                "ORDER BY CASE WHEN checked = 0 THEN 0 ELSE 1 END, isImportant DESC, date ASC "
    )
    fun getAllTasks(): Flow<List<TaskCache>>

    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE listId = :listId " +
                "ORDER BY CASE WHEN checked = 0 THEN 0 ELSE 1 END, isImportant DESC, date ASC"
    )
    fun getTasksByList(listId: Int): Flow<List<TaskCache>>

    @Query("SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskCache

    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE isImportant = 1 " +
                "ORDER by date ASC, CASE WHEN checked = 0 THEN 0 ELSE 1 END, isImportant DESC"
    )
    fun getImportantTasks(): Flow<List<TaskCache>>

    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE checked = 1 " +
                "ORDER by isImportant DESC"
    )
    fun getCompletedTasks(): Flow<List<TaskCache>>

    @Query("SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE title LIKE :query ORDER by date ASC")
    fun searchTask(query: String): Flow<List<TaskCache>>

    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE date = :today " +
                "ORDER by CASE WHEN checked = 0 THEN 0 ELSE 1 END, isImportant DESC"
    )
    fun getTodayTasks(today: Date): Flow<List<TaskCache>>

    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE date = :tomorrow " +
                "ORDER by CASE WHEN checked = 0 THEN 0 ELSE 1 END, isImportant DESC"
    )
    fun getTomorrowTasks(tomorrow: Date): Flow<List<TaskCache>>

    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE date > :tomorrowDate " +
                "ORDER by date ASC, CASE WHEN checked = 0 THEN 0 ELSE 1 END, isImportant DESC"
    )
    fun getLaterTasks(tomorrowDate: Date): Flow<List<TaskCache>>

    @Query(
        "SELECT * FROM ${CacheConstants.TASK_TABLE_NAME} WHERE date IS NULL " +
                "ORDER by CASE WHEN checked = 0 THEN 0 ELSE 1 END, isImportant DESC"
    )
    fun getNoDatesTasks(): Flow<List<TaskCache>>

    @Query("SELECT COUNT(*) FROM ${CacheConstants.TASK_TABLE_NAME} WHERE createdDate BETWEEN :startDate AND :endDate")
    suspend fun getCreatedCountByPeriod(startDate: Date, endDate: Date): Int

    @Query("SELECT COUNT(*) FROM ${CacheConstants.TASK_TABLE_NAME} WHERE checked = 1 AND createdDate BETWEEN :startDate AND :endDate")
    suspend fun getCompletedCountByPeriod(startDate: Date, endDate: Date): Int

    @Query("DELETE FROM ${CacheConstants.TASK_TABLE_NAME} WHERE listId = :listId")
    suspend fun deleteTasksByListId(listId: Int)

    @Query("DELETE FROM ${CacheConstants.TASK_TABLE_NAME} WHERE checked = 1")
    suspend fun deleteCompletedTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskCache: TaskCache): Long

    @Update
    suspend fun updateTask(taskCache: TaskCache)

    @Query("DELETE FROM ${CacheConstants.TASK_TABLE_NAME} WHERE id = :id")
    suspend fun deleteTask(id: Int)
}