package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "events", primaryKeys = ["id", "is_received_event", "repo_owner_login", "repo_full_name"])
data class EventEntity(
    val id: String,
    @ColumnInfo(name = "type")
    val type: String?,
    @Embedded(prefix = "actor_")
    val actor: UserEntity?,
    @Embedded(prefix = "repo_")
    val repo: RepositoryEntity?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "public")
    val public: Boolean?,
    @ColumnInfo(name = "org_id")
    val orgId: String? = null, // Assuming we might need to link to an organization later
    @ColumnInfo(name = "is_received_event")
    val isReceivedEvent: Boolean = false, // New field to differentiate event types
    val userLogin: String? = null, // Added for user events
    @ColumnInfo(name = "repo_owner_login")
    val repoOwnerLogin: String = "", // Added for repository events
    @ColumnInfo(name = "repo_full_name")
    val repoFullName: String = "" // Added for repository events
)
