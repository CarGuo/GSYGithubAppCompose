package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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

    fun getTrendingRepositories(language: String? = null, page: Int = 1): Flow<RepositoryResult<List<Repository>>> = flow {
        var isDbEmpty = false
        // For paginated data, we only check the DB on the first page.
        if (page == 1) {
            val cachedRepoEntities = repositoryDao.getTrendingRepositories().first()
            isDbEmpty = cachedRepoEntities.isEmpty()
            if (!isDbEmpty) {
                val cachedRepos = cachedRepoEntities.map { it.toRepository() }
                emit(RepositoryResult(Result.success(cachedRepos), DataSource.CACHE, isDbEmpty))
            }
        }

        // 2. Fetch from network
        try {
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

            val response = apiService.searchRepositories(query, page = page)

            // 3. If it's the first page, update the database
            if (page == 1) {
                repositoryDao.clearAndInsert(response.items.map { it.toEntity() })
            }

            // 4. Emit network data
            emit(RepositoryResult(Result.success(response.items), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
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

}
