package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.EventDao
import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val apiService: GitHubApiService, private val eventDao: EventDao
) {

    fun getReceivedEvents(
        username: String, page: Int = 1, isReceivedEvent: Boolean
    ): Flow<RepositoryResult<List<Event>>> = flow {
        var isDbEmpty = false
        // For paginated data, we only check the DB on the first page.
        if (page == 1) {
            val cachedEvents = eventDao.getEvents("", isReceivedEvent).map { it ->
                it.toEvent()
            }
            isDbEmpty = cachedEvents.isEmpty()
            if (cachedEvents.isNotEmpty()) {
                emit(RepositoryResult(Result.success(cachedEvents), DataSource.CACHE, isDbEmpty))
            }
        }

        // 2. Fetch from network
        try {
            val networkEvents = apiService.getReceivedEvents(username, page)
            // 3. If it's the first page, update the database
            if (page == 1) {
                val eventEntities = networkEvents.map { it ->
                    it.toEntity(isReceivedEvent)
                }

                eventDao.clearAndInsert("", isReceivedEvent, eventEntities)
            }
            // 4. Emit network data
            emit(RepositoryResult(Result.success(networkEvents), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            // Emit network failure. If cached data was already emitted, this failure will follow the cached data.
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }

    fun getRepositoryEvents(
        owner: String, repoName: String, page: Int = 1
    ): Flow<RepositoryResult<List<Event>>> = flow {
        var isDbEmpty = false
        if (page == 1) {
            val cachedEvents = eventDao.getRepoEvents(owner, repoName).map { it.toEvent() }
            isDbEmpty = cachedEvents.isEmpty()
            if (cachedEvents.isNotEmpty()) {
                emit(RepositoryResult(Result.success(cachedEvents), DataSource.CACHE, isDbEmpty))
            }
        }

        try {
            val networkEvents = apiService.getRepositoryEvents(owner, repoName, page)
            if (page == 1) {
                val eventEntities = networkEvents.map { it.toEntity(false, null, owner, repoName) }
                eventDao.clearAndInsertRepoEvents(owner, repoName, eventEntities)
            }
            emit(RepositoryResult(Result.success(networkEvents), DataSource.NETWORK, isDbEmpty))
        } catch (e: Exception) {
            emit(RepositoryResult(Result.failure(e), DataSource.NETWORK, isDbEmpty))
        }
    }
}
