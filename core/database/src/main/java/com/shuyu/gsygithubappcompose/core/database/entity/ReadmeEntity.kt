package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "readme", primaryKeys = ["owner", "repo"])
data class ReadmeEntity(
    @ColumnInfo(name = "owner")
    val owner: String,
    @ColumnInfo(name = "repo")
    val repo: String,
    @ColumnInfo(name = "data")
    val data: String,
)
