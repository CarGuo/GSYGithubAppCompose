package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.IssueDao
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.data.repository.mapper.toIssue
import com.shuyu.gsygithubappcompose.data.repository.mapper.toIssueEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepository @Inject constructor(
    private val githubApiService: GitHubApiService, private val issueDao: IssueDao
) {

    fun getRepositoryIssues(
        owner: String, repoName: String, state: String, query: String, page: Int
    ): Flow<RepositoryResult<List<Issue>>> = flow {
        var isDbEmpty: Boolean
        val dbData = issueDao.getIssues(owner, repoName, page).first()
        isDbEmpty = dbData.isEmpty()

        if (dbData.isNotEmpty() && state == "all" && query.isEmpty()) {
            emit(RepositoryResult(Result.success(dbData.toIssue()), DataSource.CACHE, isDbEmpty))
        }
        try {
            val networkIssues = if (query.isNotEmpty()) {
                // If there's a search query, use the search API
                val query = if (state == "all") {
                    "$query+repo%3A$owner%2F$repoName";
                } else {
                    "$query+repo%3A$owner%2F$repoName+state%3A$state";
                }
                githubApiService.searchIssues(
                    query = query, page = page, perPage = NetworkConfig.PER_PAGE
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

            if (page == 1 && state == "all" && query.isEmpty()) {
                issueDao.clearIssues(owner, repoName)
            }
            issueDao.insertAll(networkIssues.map { it.toIssueEntity(owner, repoName, page) })

            emit(RepositoryResult(Result.success(networkIssues), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }
}
