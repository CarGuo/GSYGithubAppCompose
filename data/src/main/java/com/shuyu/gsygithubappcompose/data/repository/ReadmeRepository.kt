package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.ReadmeDao
import com.shuyu.gsygithubappcompose.core.database.entity.ReadmeEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ReadmeRepository @Inject constructor(
    private val githubApiService: GitHubApiService,
    private val readmeDao: ReadmeDao
) {

    fun getReadme(owner: String, repo: String, branch: String?): Flow<RepositoryResult<String>> = flow {
        val cachedReadme = readmeDao.getReadme(owner, repo)
        if (cachedReadme != null) {
            emit(RepositoryResult(Result.success(cachedReadme.data), DataSource.CACHE))
        }

        try {
            val responseBody = githubApiService.getReadme(owner, repo, branch)
            val htmlContent = responseBody.string()
            readmeDao.insert(ReadmeEntity(owner, repo, htmlContent))
            emit(RepositoryResult(Result.success(htmlContent), DataSource.NETWORK))
        } catch (e: Exception) {
            emit(
                RepositoryResult(
                    Result.failure(e),
                    if (cachedReadme == null) DataSource.NETWORK else DataSource.CACHE
                )
            )
        }
    }.flowOn(Dispatchers.IO)
}
