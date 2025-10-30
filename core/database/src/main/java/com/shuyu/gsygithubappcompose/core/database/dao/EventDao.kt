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

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    suspend fun getEvents(): List<EventEntity>

    @Query("DELETE FROM events")
    suspend fun clearEvents()

    @Transaction
    suspend fun clearAndInsert(events: List<EventEntity>) {
        clearEvents()
        insert(events)
    }
}
