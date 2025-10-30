package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao {
    @Query("SELECT * FROM repositories ORDER BY stargazersCount DESC LIMIT :limit")
    fun getTrendingRepositories(limit: Int = 30): Flow<List<RepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE ownerId = :ownerId")
    fun getRepositoriesByOwner(ownerId: Long): Flow<List<RepositoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Query("DELETE FROM repositories")
    suspend fun deleteAllRepositories()

    @Transaction
    suspend fun clearAndInsert(repositories: List<RepositoryEntity>) {
        deleteAllRepositories()
        insertRepositories(repositories)
    }
}
