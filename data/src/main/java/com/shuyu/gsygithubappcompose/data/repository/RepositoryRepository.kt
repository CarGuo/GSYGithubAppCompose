package com.shuyu.gsygithubappcompose.data.repository

import com.apollographql.apollo3.ApolloClient
import com.shuyu.gsygithubappcompose.core.database.dao.CommitDao
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDao
import com.shuyu.gsygithubappcompose.core.database.dao.RepositoryDetailDao
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.graphql.GetRepositoryDetailQuery
import com.shuyu.gsygithubappcompose.core.network.model.RepoCommit
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.RepositoryDetailModel
import com.shuyu.gsygithubappcompose.core.network.model.RepositorySearchResponse
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toRepoCommit
import com.shuyu.gsygithubappcompose.data.repository.mapper.toRepository
import com.shuyu.gsygithubappcompose.data.repository.mapper.toRepositoryDetailModel
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
    private val repositoryDao: RepositoryDao,
    private val repositoryDetailDao: RepositoryDetailDao,
    private val commitDao: CommitDao, // Inject CommitDao
    private val apolloClient: ApolloClient
) {

    fun getTrendingRepositories(
        language: String? = null, page: Int = 1
    ): Flow<RepositoryResult<List<Repository>>> = flow {
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

            // 3. If it\'s the first page, update the database
            if (page == 1) {
                repositoryDao.clearAndInsert(response.items.map { it.toEntity() })
            }

            // 4. Emit network data
            emit(RepositoryResult(Result.success(response.items), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun getRepositoryDetail(
        owner: String, name: String
    ): Flow<RepositoryResult<RepositoryDetailModel>> = flow {
        var isDbEmpty = false

        // Try to get from database first
        val cachedDetail = repositoryDetailDao.getRepositoryDetail("$owner/$name")
        if (cachedDetail != null) {
            emit(
                RepositoryResult(
                    Result.success(cachedDetail.toRepositoryDetailModel()),
                    DataSource.CACHE,
                    isDbEmpty
                )
            )
        } else {
            isDbEmpty = true;
        }

        // Fetch from network
        try {
            val response = apolloClient.query(GetRepositoryDetailQuery(owner, name)).execute()
            val repository = response.data?.repository

            if (repository != null) {
                val detailEntity = repository.toEntity()
                repositoryDetailDao.insert(detailEntity)
                emit(
                    RepositoryResult(
                        Result.success(detailEntity.toRepositoryDetailModel()),
                        DataSource.NETWORK,
                        isDbEmpty
                    )
                )
            } else {
                emit(
                    RepositoryResult(
                        Result.failure(Throwable("Repository null")),
                        DataSource.NETWORK,
                        isDbEmpty
                    )
                )
            }
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

    fun getRepoCommits(
        owner: String, repoName: String, page: Int = 1
    ): Flow<RepositoryResult<List<RepoCommit>>> = flow {
        var isDbEmpty = false
        if (page == 1) {
            val cachedCommits = commitDao.getRepoCommits(owner, repoName).map { it.toRepoCommit() }
            isDbEmpty = cachedCommits.isEmpty()
            if (cachedCommits.isNotEmpty()) {
                emit(RepositoryResult(Result.success(cachedCommits), DataSource.CACHE, isDbEmpty))
            }
        }

        try {
            val networkCommits = apiService.getRepositoryCommits(owner, repoName, page)
            if (page == 1) {
                val commitEntities = networkCommits.map { it.toEntity(owner, repoName) }
                commitDao.clearAndInsertRepoCommits(owner, repoName, commitEntities)
            }
            emit(RepositoryResult(Result.success(networkCommits), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun getCachedTrendingRepositories(limit: Int = 30): Flow<List<RepositoryEntity>> {
        return repositoryDao.getTrendingRepositories(limit)
    }

    suspend fun searchRepositories(query: String, page: Int = 1): RepositorySearchResponse {
        return apiService.searchRepositories(query, page = page)
    }
}
