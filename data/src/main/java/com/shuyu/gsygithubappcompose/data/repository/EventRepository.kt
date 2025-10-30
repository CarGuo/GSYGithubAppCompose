package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.EventDao
import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity
import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.EventPayload
import com.shuyu.gsygithubappcompose.core.network.model.EventRepo
import com.shuyu.gsygithubappcompose.core.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val eventDao: EventDao
) {

    fun getReceivedEvents(username: String, page: Int = 1): Flow<Result<List<Event>>> = flow {
        // 1. Emit data from database
        val cachedEvents = eventDao.getEvents().map {
            Event(
                id = it.id,
                type = it.type,
                actor = User(id = it.actorId, login = it.actorLogin, avatarUrl = it.actorAvatarUrl, name = null, bio = null, company = null, blog = null, location = null, email = null, publicRepos = 0, followers = 0, following = 0, createdAt = "", updatedAt = ""),
                repo = EventRepo(id = it.repoId, name = it.repoName, url = ""),
                payload = EventPayload(action = it.payload, refType = null, masterBranch = null, description = null, pusherType = null),
                createdAt = it.createdAt
            )
        }
        emit(Result.success(cachedEvents))

        // 2. Fetch from network
        try {
            val networkEvents = apiService.getReceivedEvents(username, page)
            // 3. If it's the first page, update the database
            if (page == 1) {
                val eventEntities = networkEvents.map {
                    EventEntity(
                        id = it.id,
                        type = it.type ,
                        actorId = it.actor.id,
                        actorLogin = it.actor.login,
                        actorAvatarUrl = it.actor.avatarUrl,
                        repoId = it.repo.id,
                        repoName = it.repo.name,
                        payload = it.payload?.action ?: "",
                        createdAt = it.createdAt
                    )
                }
                eventDao.clearAndInsert(eventEntities)
            }
            // 4. Emit network data
            emit(Result.success(networkEvents))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
