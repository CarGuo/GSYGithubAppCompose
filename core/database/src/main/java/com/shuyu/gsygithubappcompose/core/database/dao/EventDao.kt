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

    @Query("SELECT * FROM events WHERE is_received_event = 0 AND userLogin = :userLogin")
    suspend fun getEventsByUserLogin(userLogin: String): List<EventEntity>

    @Query("DELETE FROM events WHERE is_received_event = 0 AND userLogin = :userLogin")
    suspend fun clearUserEvents(userLogin: String)

    @Transaction
    suspend fun clearAndInsertUserEvents(userLogin: String, events: List<EventEntity>) {
        clearUserEvents(userLogin)
        insert(events)
    }

    // New methods for repository events
    @Query("SELECT * FROM events WHERE repo_owner_login = :owner AND repo_name = :repoName")
    suspend fun getRepoEvents(owner: String, repoName: String): List<EventEntity>

    @Query("DELETE FROM events WHERE repo_owner_login = :owner AND repo_name = :repoName")
    suspend fun clearRepoEvents(owner: String, repoName: String)

    @Transaction
    suspend fun clearAndInsertRepoEvents(owner: String, repoName: String, events: List<EventEntity>) {
        clearRepoEvents(owner, repoName)
        insert(events)
    }

    @Query("DELETE FROM events")
    suspend fun clearAll()
}
