package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class User(
    val login: String,
    val id: Long,
    @SerializedName("node_id")
    val nodeId: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("gravatar_id")
    val gravatarId: String?,
    val url: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("followers_url")
    val followersUrl: String?,
    @SerializedName("following_url")
    val followingUrl: String?,
    @SerializedName("gists_url")
    val gistsUrl: String?,
    @SerializedName("starred_url")
    val starredUrl: String?,
    @SerializedName("subscriptions_url")
    val subscriptionsUrl: String?,
    @SerializedName("organizations_url")
    val organizationsUrl: String?,
    @SerializedName("repos_url")
    val reposUrl: String?,
    @SerializedName("events_url")
    val eventsUrl: String?,
    @SerializedName("received_events_url")
    val receivedEventsUrl: String?,
    val type: String?,
    @SerializedName("site_admin")
    val siteAdmin: Boolean?,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val starred: String?,
    val bio: String?,
    @SerializedName("public_repos")
    val publicRepos: Int?,
    @SerializedName("public_gists")
    val publicGists: Int?,
    val followers: Int?,
    val following: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("private_gists")
    val privateGists: Int?,
    @SerializedName("total_private_repos")
    val totalPrivateRepos: Int?,
    @SerializedName("owned_private_repos")
    val ownedPrivateRepos: Int?,
    @SerializedName("disk_usage")
    val diskUsage: Int?,
    val collaborators: Int?,
    @SerializedName("two_factor_authentication")
    val twoFactorAuthentication: Boolean?
) {
    companion object {
        fun toMiniUserModel(
            login: String,
            id: Long,
            avatarUrl: String,
            nodeId: String? = null,
            gravatarId: String? = null,
            url: String? = null,
            htmlUrl: String? = null,
            followersUrl: String? = null,
            followingUrl: String? = null,
            gistsUrl: String? = null,
            starredUrl: String? = null,
            subscriptionsUrl: String? = null,
            organizationsUrl: String? = null,
            reposUrl: String? = null,
            eventsUrl: String? = null,
            receivedEventsUrl: String? = null,
            type: String? = null,
            siteAdmin: Boolean? = null,
            name: String? = null,
            company: String? = null,
            blog: String? = null,
            location: String? = null,
            email: String? = null,
            starred: String? = null,
            bio: String? = null,
            publicRepos: Int? = null,
            publicGists: Int? = null,
            followers: Int? = null,
            following: Int? = null,
            createdAt: String? = null,
            updatedAt: String? = null,
            privateGists: Int? = null,
            totalPrivateRepos: Int? = null,
            ownedPrivateRepos: Int? = null,
            diskUsage: Int? = null,
            collaborators: Int? = null,
            twoFactorAuthentication: Boolean? = null
        ): User {
            return User(
                login = login,
                id = id,
                nodeId = nodeId,
                avatarUrl = avatarUrl,
                gravatarId = gravatarId,
                url = url,
                htmlUrl = htmlUrl,
                followersUrl = followersUrl,
                followingUrl = followingUrl,
                gistsUrl = gistsUrl,
                starredUrl = starredUrl,
                subscriptionsUrl = subscriptionsUrl,
                organizationsUrl = organizationsUrl,
                reposUrl = reposUrl,
                eventsUrl = eventsUrl,
                receivedEventsUrl = receivedEventsUrl,
                type = type,
                siteAdmin = siteAdmin,
                name = name,
                company = company,
                blog = blog,
                location = location,
                email = email,
                starred = starred,
                bio = bio,
                publicRepos = publicRepos,
                publicGists = publicGists,
                followers = followers,
                following = following,
                createdAt = createdAt,
                updatedAt = updatedAt,
                privateGists = privateGists,
                totalPrivateRepos = totalPrivateRepos,
                ownedPrivateRepos = ownedPrivateRepos,
                diskUsage = diskUsage,
                collaborators = collaborators,
                twoFactorAuthentication = twoFactorAuthentication
            )
        }
    }
}

