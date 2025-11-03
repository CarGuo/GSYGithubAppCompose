package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.EventDao
import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.EventPayload
import com.shuyu.gsygithubappcompose.core.network.model.EventRepo
import com.shuyu.gsygithubappcompose.core.network.model.User
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
    ): Flow<Result<List<Event>>> = flow {
        // 1. Emit data from database
        val cachedEvents = eventDao.getEvents("*me", true).map { it ->
            it.toEvent()
        }
        emit(Result.success(cachedEvents))

        // 2. Fetch from network
        try {
            val networkEvents = apiService.getReceivedEvents(username, page)
            // 3. If it's the first page, update the database
            if (page == 1) {
                val eventEntities = networkEvents.map { it ->
                    it.toEntity(isReceivedEvent)
                }
                eventDao.clearAndInsert("*me", true, eventEntities)
            }
            // 4. Emit network data
            emit(Result.success(networkEvents))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
