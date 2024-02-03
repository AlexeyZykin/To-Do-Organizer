package com.example.vkr_todolist.cache.room.dao

import androidx.room.*
import com.example.vkr_todolist.cache.room.model.Event
import com.example.vkr_todolist.cache.room.model.Note
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface EventDao {
    @Query("SELECT * FROM event order by date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM event WHERE listId = :listId")
    fun getEventsByList(listId: Int): Flow<List<Event>>

    @Query("DELETE FROM event WHERE listId = :listId")
    suspend fun deleteEventsByListId(listId: Int)

    @Query("SELECT * FROM event WHERE eventTitle LIKE :query")
    fun searchEvent(query: String): Flow<List<Event>>

    @Query("DELETE FROM event WHERE date < :currentDate")
    suspend fun deleteEndedEvents(currentDate: Date)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Insert
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)
}