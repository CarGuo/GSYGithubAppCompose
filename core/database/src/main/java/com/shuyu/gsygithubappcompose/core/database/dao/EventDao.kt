package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("SELECT * FROM events WHERE is_received_event = :isReceivedEvent AND (:isReceivedEvent = 1 OR actor_login = :login)")
    suspend fun getEvents(login: String, isReceivedEvent: Boolean): List<EventEntity>

    @Query("DELETE FROM events WHERE is_received_event = :isReceivedEvent AND (:isReceivedEvent = 1 OR actor_login = :login)")
    suspend fun clearEvents(login: String, isReceivedEvent: Boolean)

    @Transaction
    suspend fun clearAndInsert(login: String, isReceivedEvent: Boolean, events: List<EventEntity>) {
        clearEvents(login, isReceivedEvent)
        insert(events)
    }

    @Query("SELECT * FROM events WHERE userLogin = :userLogin")
    suspend fun getEventsByUserLogin(userLogin: String): List<EventEntity>

    @Query("DELETE FROM events WHERE userLogin = :userLogin")
    suspend fun clearUserEvents(userLogin: String)

    @Transaction
    suspend fun clearAndInsertUserEvents(userLogin: String, events: List<EventEntity>) {
        clearUserEvents(userLogin)
        insert(events)
    }
}
