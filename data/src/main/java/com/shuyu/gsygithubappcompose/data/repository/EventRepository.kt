package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.network.api.GitHubApiService
import com.shuyu.gsygithubappcompose.core.network.model.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val apiService: GitHubApiService
) {
    
    suspend fun getReceivedEvents(username: String, page: Int = 1): Result<List<Event>> {
        return try {
            val events = apiService.getReceivedEvents(username, page)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
