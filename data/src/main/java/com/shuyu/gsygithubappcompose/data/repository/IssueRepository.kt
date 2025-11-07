package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.IssueCommentDao
import com.shuyu.gsygithubappcompose.core.database.dao.IssueDao
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Comment
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.data.repository.mapper.toIssue
import com.shuyu.gsygithubappcompose.data.repository.mapper.toIssueComment
import com.shuyu.gsygithubappcompose.data.repository.mapper.toIssueCommentEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toIssueEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepository @Inject constructor(
    private val githubApiService: GitHubApiService,
    private val issueDao: IssueDao,
    private val issueCommentDao: IssueCommentDao
) {

    fun getRepositoryIssues(
        owner: String, repoName: String, state: String, query: String, page: Int
    ): Flow<RepositoryResult<List<Issue>>> = flow {
        var isDbEmpty = true

        if (query.isEmpty() && state == "all") {
            val dbData = issueDao.getIssues(owner, repoName).first()
            isDbEmpty = dbData.isEmpty()
            if (!isDbEmpty) {
                emit(
                    RepositoryResult(
                        Result.success(dbData.map { it.toIssue() }), DataSource.CACHE, isDbEmpty
                    )
                )
            }
        }
        try {
            val networkIssues = if (query.isNotEmpty()) {
                // If there's a search query, use the search API
                val searchQuery = if (state == "all") {
                    "$query+repo:$owner/$repoName"
                } else {
                    "$query+repo:$owner/$repoName+state:$state"
                }
                githubApiService.searchIssues(
                    query = searchQuery, page = page, perPage = NetworkConfig.PER_PAGE
                ).items
            } else {
                // Otherwise, use the getRepositoryIssues API
                githubApiService.getRepositoryIssues(
                    reposOwner = owner,
                    reposName = repoName,
                    state = state,
                    page = page,
                    perPage = NetworkConfig.PER_PAGE
                )
            }

            if (query.isEmpty() && page == 1 && state == "all") {
                issueDao.clearIssues(owner, repoName)
                issueDao.insertAll(networkIssues.map { it.toIssueEntity(owner, repoName) })
            }

            emit(RepositoryResult(Result.success(networkIssues), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun getIssueInfo(
        owner: String, repoName: String, issueNumber: Int
    ): Flow<RepositoryResult<Issue>> = flow {
        var isDbEmpty: Boolean
        val dbData = issueDao.getIssueById(owner, repoName, issueNumber.toLong()).first()
        isDbEmpty = (dbData == null)

        if (dbData != null) {
            emit(RepositoryResult(Result.success(dbData.toIssue()), DataSource.CACHE, isDbEmpty))
        }

        try {
            val networkIssue = githubApiService.getIssueInfo(
                acceptHeader = "application/vnd.github.VERSION.raw",
                reposOwner = owner,
                reposName = repoName,
                issueNumber = issueNumber
            )
            issueDao.insertIssue(networkIssue.toIssueEntity(owner, repoName))
            emit(RepositoryResult(Result.success(networkIssue), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun getIssueComments(
        owner: String, repoName: String, issueNumber: Int, page: Int
    ): Flow<RepositoryResult<List<Comment>>> = flow {
        var isDbEmpty: Boolean
        val dbData = issueCommentDao.getIssueComments(issueNumber.toLong()).first()
        isDbEmpty = dbData.isEmpty()

        if (dbData.isNotEmpty() && page == 1) {
            emit(
                RepositoryResult(
                    Result.success(dbData.map { it.toIssueComment() }), DataSource.CACHE, isDbEmpty
                )
            )
        }

        try {
            val networkComments = githubApiService.getIssueComments(
                reposOwner = owner,
                reposName = repoName,
                issueNumber = issueNumber,
                page = page,
                perPage = NetworkConfig.PER_PAGE
            )

            if (page == 1) {
                issueCommentDao.clearIssueComments(issueNumber.toLong())
            }
            issueCommentDao.insertAll(networkComments.map { it.toIssueCommentEntity(issueNumber.toLong()) })

            emit(RepositoryResult(Result.success(networkComments), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun addIssueComment(
        owner: String, repoName: String, issueNumber: Int, body: String
    ): Flow<RepositoryResult<Comment>> = flow {
        try {
            val comment = mapOf("body" to body)
            val newComment = githubApiService.addIssueComment(owner, repoName, issueNumber, comment)
            emit(RepositoryResult(Result.success(newComment), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }

    fun editIssue(
        owner: String,
        repoName: String,
        issueNumber: Int,
        title: String,
        body: String,
        state: String? = null
    ): Flow<RepositoryResult<Issue>> = flow {
        try {
            val issueMap: Map<String, String> = if (state == null) {
                mapOf("title" to title, "body" to body)
            } else {
                mapOf("state" to state)
            }
            val updatedIssue = githubApiService.editIssue(owner, repoName, issueNumber, issueMap)
            emit(RepositoryResult(Result.success(updatedIssue), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }

    fun lockIssue(
        owner: String, repoName: String, issueNumber: Int
    ): Flow<RepositoryResult<Unit>> = flow {
        try {
            githubApiService.lockIssue(owner, repoName, issueNumber)
            emit(RepositoryResult(Result.success(Unit), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }

    fun unlockIssue(
        owner: String, repoName: String, issueNumber: Int
    ): Flow<RepositoryResult<Unit>> = flow {
        try {
            githubApiService.unlockIssue(owner, repoName, issueNumber)
            emit(RepositoryResult(Result.success(Unit), DataSource.NETWORK, true))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, true))
        }
    }
}
