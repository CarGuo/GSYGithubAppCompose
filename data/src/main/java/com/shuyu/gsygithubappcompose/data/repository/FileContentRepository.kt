package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.FileContentDao
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.FileContent
import com.shuyu.gsygithubappcompose.data.repository.mapper.toFileContentEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toFileContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileContentRepository @Inject constructor(
    private val gitHubApiService: GitHubApiService,
    private val fileContentDao: FileContentDao
) {

    fun getRepositoryContents(
        owner: String,
        repo: String,
        path: String = ""
    ): Flow<RepositoryResult<List<FileContent>>> = flow {
        val isRootPath = path.isEmpty()
        var dbWasEmpty = true

        if (isRootPath) {
            val cachedData = fileContentDao.getFileContents(owner, repo, path)
            if (cachedData.isNotEmpty()) {
                dbWasEmpty = false
                emit(RepositoryResult(Result.success(cachedData.map { it.toFileContent() }), DataSource.CACHE, isDbEmpty = false))
            }
        }

        try {
            val networkData = gitHubApiService.getRepositoryContents(owner, repo, path)
            if (isRootPath) {
                fileContentDao.deleteAllFileContents(owner, repo)
                fileContentDao.insertAll(networkData.map { it.toFileContentEntity(owner, repo) })
            }
            emit(RepositoryResult(Result.success(networkData), DataSource.NETWORK, isDbEmpty = dbWasEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty = dbWasEmpty))
        }
    }
}
