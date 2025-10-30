package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    fun getTrendingRepositories(language: String? = null, page: Int = 1): Flow<Result<List<Repository>>> = flow {
        // 1. Emit data from database
        val cachedRepos = repositoryDao.getTrendingRepositories().map {
            it.map { repoEntity ->
                Repository(
                    id = repoEntity.id,
                    name = repoEntity.name,
                    fullName = repoEntity.fullName,
                    description = repoEntity.description,
                    owner = User(id = repoEntity.ownerId, login = repoEntity.ownerLogin, avatarUrl = repoEntity.ownerAvatarUrl, name = null, bio = null, company = null, blog = null, location = null, email = null, publicRepos = 0, followers = 0, following = 0, createdAt = "", updatedAt = ""),
                    private = repoEntity.isPrivate,
                    htmlUrl = repoEntity.htmlUrl,
                    language = repoEntity.language,
                    stargazersCount = repoEntity.stargazersCount,
                    watchersCount = repoEntity.watchersCount,
                    forksCount = repoEntity.forksCount,
                    openIssuesCount = repoEntity.openIssuesCount,
                    createdAt = repoEntity.createdAt,
                    updatedAt = repoEntity.updatedAt,
                    pushedAt = ""
                )
            }
        }
        //不需要collect阻塞
        //cachedRepos.collect { emit(Result.success(it)) }
        emit(Result.success(cachedRepos.first()))

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

            val response = apiService.getTrendingRepositories(query, page = page)

            // 3. If it's the first page, update the database
            if (page == 1) {
                repositoryDao.clearAndInsert(response.items.map { it.toEntity() })
            }

            // 4. Emit network data
            emit(Result.success(response.items))
        } catch (e: Exception) {
            emit(Result.failure(e))
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
