package com.shuyu.gsygithubappcompose.core.network.api

import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.AccessToken
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.RepositorySearchResponse
import com.shuyu.gsygithubappcompose.core.network.model.User
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * GitHub API service
 */
interface GitHubApiService {
    
    /**
     * Exchange OAuth code for access token
     */
    @POST("https://github.com/login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String
    ): AccessToken
    
    /**
     * Get authenticated user
     */
    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") token: String
    ): User
    
    /**
     * Get user by username
     */
    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): User
    
    /**
     * Get user received events
     */
    @GET("users/{username}/received_events")
    suspend fun getReceivedEvents(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Event>


    /**
     * Get user events
     */
    @GET("users/{username}/events")
    suspend fun getUserEvents(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Event>

    /**
     * Get organization members
     */
    @GET("orgs/{org}/members")
    suspend fun getOrgMembers(
        @Path("org") org: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<User>


    /**
     * Get trending repositories
     */
    @GET("search/repositories")
    suspend fun getTrendingRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): RepositorySearchResponse
    
    /**
     * Get user repositories
     */
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Repository>

}
