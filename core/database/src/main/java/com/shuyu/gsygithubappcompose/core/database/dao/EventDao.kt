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

    @Query("SELECT * FROM events WHERE actor_login = :login AND is_received_event = :isReceivedEvent")
    suspend fun getEvents(login: String, isReceivedEvent: Boolean): List<EventEntity>

    @Query("DELETE FROM events WHERE actor_login = :login AND is_received_event = :isReceivedEvent")
    suspend fun clearEvents(login: String, isReceivedEvent: Boolean)

    @Transaction
    suspend fun clearAndInsert(login: String, isReceivedEvent: Boolean, events: List<EventEntity>) {
        clearEvents(login, isReceivedEvent)
        insert(events)
    }
}
