package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val repositoryDao: RepositoryDao
) {
    
    suspend fun getTrendingRepositories(language: String? = null): Result<List<Repository>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = dateFormat.format(calendar.time)
            
            val query = buildString {
                append("created:>$date")
                if (language != null) {
                    append(" language:$language")
                }
            }
            
            val response = apiService.getTrendingRepositories(query)
            
            // Cache in database
            repositoryDao.insertRepositories(response.items.map { it.toEntity() })
            
            Result.success(response.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserRepositories(username: String): Result<List<Repository>> {
        return try {
            val repos = apiService.getUserRepositories(username)
            repositoryDao.insertRepositories(repos.map { it.toEntity() })
            Result.success(repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCachedTrendingRepositories(limit: Int = 30): Flow<List<RepositoryEntity>> {
        return repositoryDao.getTrendingRepositories(limit)
    }
    
    private fun Repository.toEntity() = RepositoryEntity(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        ownerId = owner.id,
        ownerLogin = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        isPrivate = private,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
