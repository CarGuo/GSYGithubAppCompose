package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.PushCommitDao
import com.shuyu.gsygithubappcompose.core.database.entity.PushCommitEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.PushCommit
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PushRepository @Inject constructor(
    private val githubApiService: GitHubApiService, private val pushCommitDao: PushCommitDao
) {

    fun getRepositoryCommitInfo(
        owner: String, repo: String, sha: String
    ): Flow<RepositoryResult<PushCommit>> {
        return flow {
            val cachedCommit = pushCommitDao.getPushCommit(sha)
            if (cachedCommit != null) {
                emit(
                    RepositoryResult(
                        Result.success(cachedCommit.toModel()), DataSource.CACHE, false
                    )
                )
            }

            try {
                val networkCommit = githubApiService.getRepositoryCommitInfo(
                    reposOwner = owner, reposName = repo, sha = sha
                )
                val entity = networkCommit.toEntity()
                pushCommitDao.insertPushCommit(entity)
                emit(
                    RepositoryResult(
                        Result.success(networkCommit), DataSource.NETWORK, cachedCommit == null
                    )
                )
            } catch (e: Exception) {
                emit(
                    RepositoryResult(
                        Result.failure(e),
                        if (cachedCommit != null) DataSource.CACHE else DataSource.NETWORK,
                        cachedCommit == null
                    )
                )
            }
        }
    }
}