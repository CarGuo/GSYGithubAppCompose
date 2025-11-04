package com.shuyu.gsygithubappcompose.core.network.api

import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.model.AccessToken
import com.shuyu.gsygithubappcompose.core.network.model.Branch
import com.shuyu.gsygithubappcompose.core.network.model.Comment
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.FileContent
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.core.network.model.IssueSearchResponse
import com.shuyu.gsygithubappcompose.core.network.model.Notification
import com.shuyu.gsygithubappcompose.core.network.model.Organization
import com.shuyu.gsygithubappcompose.core.network.model.PushCommit
import com.shuyu.gsygithubappcompose.core.network.model.Release
import com.shuyu.gsygithubappcompose.core.network.model.RepoCommit
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.RepositorySearchResponse
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.core.network.model.UserSearchResponse
import com.shuyu.gsygithubappcompose.core.network.model.TrendingRepoModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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
     * Get trending repositories (general search)
     */
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "best match",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): RepositorySearchResponse

    /**
     * Search users
     */
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): UserSearchResponse


    /**
     * Get user repositories
     */
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("sort") sort: String = "pushed",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Repository>

    /**
     * Get repository detail
     */
    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetail(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Repository

    /**
     * Get repository events
     */
    @GET("networks/{owner}/{repo}/events")
    suspend fun getRepositoryEvents(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Event>

    /**
     * Get repository forks
     */
    @GET("repos/{owner}/{repo}/forks")
    suspend fun getRepositoryForks(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Repository>

    /**
     * Get repository stargazers
     */
    @GET("repos/{owner}/{repo}/stargazers")
    suspend fun getRepositoryStargazers(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<User>

    /**
     * Get repository watchers
     */
    @GET("repos/{owner}/{repo}/subscribers")
    suspend fun getRepositoryWatchers(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<User>

    /**
     * Get repository commits
     */
    @GET("repos/{owner}/{repo}/commits")
    suspend fun getRepositoryCommits(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<RepoCommit>

    /**
     * Get repository commit info
     */
    @GET("repos/{owner}/{repo}/commits/{sha}")
    suspend fun getRepositoryCommitInfo(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("sha") sha: String
    ): PushCommit

    /**
     * Get repository issues
     */
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getRepositoryIssues(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("state") state: String = "all",
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Issue>

    /**
     * Get repository releases
     */
    @GET("repos/{owner}/{repo}/releases")
    suspend fun getRepositoryReleases(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Release>

    /**
     * Get repository tags
     */
    @GET("repos/{owner}/{repo}/tags")
    suspend fun getRepositoryTags(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Any> // No specific Tag model found, keeping Any for now

    /**
     * Get repository contributors
     */
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getRepositoryContributors(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<User>

    /**
     * Get issue comments
     */
    @GET("repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun getIssueComments(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("issue_number") issueNumber: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Comment>

    /**
     * Get issue info
     */
    @GET("repos/{owner}/{repo}/issues/{issue_number}")
    suspend fun getIssueInfo(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("issue_number") issueNumber: Int
    ): Issue

    /**
     * Add issue comment
     */
    @POST("repos/{owner}/{repo}/issues/{issue_number}/comments")
    suspend fun addIssueComment(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("issue_number") issueNumber: Int,
        @Body comment: Map<String, String>
    ): Comment

    /**
     * Edit issue
     */
    @PATCH("repos/{owner}/{repo}/issues/{issue_number}")
    suspend fun editIssue(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("issue_number") issueNumber: Int,
        @Body issue: Map<String, Any>
    ): Issue

    /**
     * Lock issue
     */
    @PUT("repos/{owner}/{repo}/issues/{issue_number}/lock")
    suspend fun lockIssue(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("issue_number") issueNumber: Int
    ): Unit

    /**
     * Create issue
     */
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Body issue: Map<String, Any>
    ): Issue

    /**
     * Search issues
     */
    @GET("search/issues")
    suspend fun searchIssues(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): IssueSearchResponse

    /**
     * Edit comment
     */
    @PATCH("repos/{owner}/{repo}/issues/comments/{comment_id}")
    suspend fun editComment(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("comment_id") commentId: Int,
        @Body comment: Map<String, String>
    ): Comment

    /**
     * Delete comment
     */
    @DELETE("repos/{owner}/{repo}/issues/comments/{comment_id}")
    suspend fun deleteComment(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("comment_id") commentId: Int
    ): Unit

    /**
     * Get authenticated user's starred repositories
     */
    @GET("user/starred")
    suspend fun getMyStarredRepositories(
        @Query("sort") sort: String = "updated",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Repository>

    /**
     * Get user's starred repositories
     */
    @GET("users/{username}/starred")
    suspend fun getUserStarredRepositories(
        @Path("username") username: String,
        @Query("sort") sort: String = "updated",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Repository>

    /**
     * Check if starring a repository
     */
    @GET("user/starred/{owner}/{repo}")
    suspend fun checkStarringRepository(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Unit

    /**
     * Star a repository
     */
    @PUT("user/starred/{owner}/{repo}")
    suspend fun starRepository(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Unit

    /**
     * Unstar a repository
     */
    @DELETE("user/starred/{owner}/{repo}")
    suspend fun unstarRepository(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Unit

    /**
     * Check if watching a repository
     */
    @GET("user/subscriptions/{owner}/{repo}")
    suspend fun checkWatchingRepository(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Unit

    /**
     * Watch a repository
     */
    @PUT("user/subscriptions/{owner}/{repo}")
    suspend fun watchRepository(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Unit

    /**
     * Unwatch a repository
     */
    @DELETE("user/subscriptions/{owner}/{repo}")
    suspend fun unwatchRepository(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Unit

    /**
     * Get repository contents
     */
    @GET("repos/{owner}/{repo}/contents")
    suspend fun getRepositoryContents(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("ref") branch: String? = null // branch or tag name
    ): List<FileContent>

    /**
     * Get repository contents at a specific path
     */
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getRepositoryContentsAtPath(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Path("path") path: String,
        @Query("ref") branch: String? = null // branch or tag name
    ): FileContent

    /**
     * Get README file
     */
    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("ref") branch: String? = null // branch or tag name
    ): FileContent

    /**
     * Check if following a user
     */
    @GET("user/following/{username}")
    suspend fun checkFollowingUser(
        @Path("username") username: String
    ): Unit

    /**
     * Follow a user
     */
    @PUT("user/following/{username}")
    suspend fun followUser(
        @Path("username") username: String
    ): Unit

    /**
     * Unfollow a user
     */
    @DELETE("user/following/{username}")
    suspend fun unfollowUser(
        @Path("username") username: String
    ): Unit

    /**
     * Get user's following
     */
    @GET("users/{username}/following")
    suspend fun getUserFollowing(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<User>

    /**
     * Get authenticated user's followers
     */
    @GET("user/followers")
    suspend fun getMyFollowers(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<User>

    /**
     * Get user's followers
     */
    @GET("users/{username}/followers")
    suspend fun getUserFollowers(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<User>

    /**
     * Create a fork for a repository
     */
    @POST("repos/{owner}/{repo}/forks")
    suspend fun createFork(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String
    ): Repository

    /**
     * Get repository branches
     */
    @GET("repos/{owner}/{repo}/branches")
    suspend fun getRepositoryBranches(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Branch>

    /**
     * Get repository forks with sort
     */
    @GET("repos/{owner}/{repo}/forks")
    suspend fun getRepositoryForksSorted(
        @Path("owner") reposOwner: String,
        @Path("repo") reposName: String,
        @Query("sort") sort: String = "newest",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Repository>

    /**
     * Get user organizations
     */
    @GET("users/{username}/orgs")
    suspend fun getUserOrganizations(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Organization>

    /**
     * Get notifications
     */
    @GET("notifications")
    suspend fun getNotifications(
        @Query("all") all: Boolean? = null,
        @Query("participating") participating: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = NetworkConfig.PER_PAGE
    ): List<Notification>

    /**
     * Mark notification as read
     */
    @PATCH("notifications/threads/{thread_id}")
    suspend fun markNotificationAsRead(
        @Path("thread_id") threadId: String
    ): Unit

    /**
     * Mark all notifications as read
     */
    @PUT("notifications")
    suspend fun markAllNotificationsAsRead(): Unit

    /**
     * Get trending repositories from custom API
     */
    @GET("https://guoshuyu.cn/github/trend/list")
    suspend fun getTrendingRepos(
        @Header("api-token") apiToken: String = NetworkConfig.API_TOKEN,
        @Query("since") since: String,
        @Query("languageType") languageType: String?
    ): List<TrendingRepoModel>

}
