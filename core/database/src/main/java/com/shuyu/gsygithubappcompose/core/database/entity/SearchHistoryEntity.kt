package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val query: String,
    val timestamp: Long
)
