package com.shuyu.gsygithubappcompose.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.shuyu.gsygithubappcompose.core.database.converter.Converters

@Entity(tableName = "push_commits")
@TypeConverters(Converters::class)
data class PushCommitEntity(
    @PrimaryKey
    val sha: String,
    val url: String?,
    val htmlUrl: String?,
    val commentsUrl: String?,

    @Embedded(prefix = "stats_")
    val stats: CommitStatsEntity?,

    @Embedded(prefix = "commit_")
    val commit: CommitDetailEntity?,

    @Embedded(prefix = "author_")
    val author: UserEntity?,

    @Embedded(prefix = "committer_")
    val committer: UserEntity?,

    val files: List<CommitFileEntity>?
)

data class CommitDetailEntity(
    @Embedded(prefix = "author_")
    val author: CommitUserEntity?,
    @Embedded(prefix = "committer_")
    val committer: CommitUserEntity?,
    val message: String,
    @Embedded(prefix = "tree_")
    val tree: CommitTreeEntity?,
    val url: String?,
    val commentCount: Int?,
    // val verification: CommitVerification? // Not including CommitVerification for now
)

data class CommitFileEntity(
    val sha: String? = null,
    val filename: String? = null,
    val status: String? = null,
    val additions: Int? = null,
    val deletions: Int? = null,
    val changes: Int? = null,
    val blobUrl: String? = null,
    val rawUrl: String? = null,
    val contentsUrl: String? = null,
    val patch: String? = null
)
data class CommitStatsEntity(
    val total: Int? = null,
    val additions: Int? = null,
    val deletions: Int? = null
)
data class CommitTreeEntity(
    val sha: String,
    val url: String?
)
