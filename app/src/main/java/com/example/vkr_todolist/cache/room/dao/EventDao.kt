package com.example.vkr_todolist.cache.room.dao

import androidx.core.location.LocationRequestCompat.Quality
import androidx.room.*
import com.example.vkr_todolist.cache.room.constants.CacheConstants
import com.example.vkr_todolist.cache.room.model.EventCache
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface EventDao {
    @Query("SELECT * FROM event order by date ASC")
    fun getAllEvents(): Flow<List<EventCache>>

    @Query("SELECT * FROM ${CacheConstants.Event_TABLE_NAME} WHERE listId = :listId")
    fun getEventsByList(listId: Int): Flow<List<EventCache>>

    @Query("SELECT * FROM ${CacheConstants.Event_TABLE_NAME} WHERE id = :id")
    suspend fun getEventById(id: Int): EventCache

    @Query("DELETE FROM ${CacheConstants.Event_TABLE_NAME} WHERE listId = :listId")
    suspend fun deleteEventsByListId(listId: Int)

    @Query("SELECT * FROM ${CacheConstants.Event_TABLE_NAME} WHERE title LIKE :query")
    fun searchEvent(query: String): Flow<List<EventCache>>

    @Query("DELETE FROM ${CacheConstants.Event_TABLE_NAME} WHERE date < :currentDate")
    suspend fun deleteEndedEvents(currentDate: Date)

    @Query("DELETE FROM ${CacheConstants.Event_TABLE_NAME} WHERE id = :id")
    suspend fun deleteEvent(id: Int)

    @Insert
    suspend fun insertEvent(eventCache: EventCache): Long

    @Update
    suspend fun updateEvent(eventCache: EventCache)
}