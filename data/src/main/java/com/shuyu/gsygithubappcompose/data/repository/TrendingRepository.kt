package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.TrendingDao
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.data.repository.mapper.toTrendingEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toTrendingRepoModel
import com.shuyu.gsygithubappcompose.core.network.model.TrendingRepoModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrendingRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val trendingDao: TrendingDao
) {

    fun getTrendingRepositories(since: String, languageType: String?): Flow<RepositoryResult<List<TrendingRepoModel>>> = flow {
        var isDbEmpty = false

        // 1. Try to load from database first
        val cachedTrendingEntities = trendingDao.getAllTrendingRepos()
        isDbEmpty = cachedTrendingEntities.isEmpty()
        if (!isDbEmpty) {
            val cachedTrendingModels = cachedTrendingEntities.map { it.toTrendingRepoModel() }
            emit(RepositoryResult(Result.success(cachedTrendingModels), DataSource.CACHE, isDbEmpty))
        }

        // 2. Fetch from network
        try {
            val response = apiService.getTrendingRepos(since = since, languageType = languageType)

            // 3. Clear old data and insert new data into database
            trendingDao.clearTrendingRepos()
            trendingDao.insertAll(response.map { it.toTrendingEntity() })

            // 4. Emit network data
            emit(RepositoryResult(Result.success(response.map { it }), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }
}
