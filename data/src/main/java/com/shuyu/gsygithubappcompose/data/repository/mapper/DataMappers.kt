package com.shuyu.gsygithubappcompose.data.repository.mapper

import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.database.entity.TrendingEntity
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.EventRepo
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.TrendingRepoModel
import com.shuyu.gsygithubappcompose.core.network.model.User
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        login = login,
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

fun User.toEntity(orgLogin: String): UserEntity {
    return UserEntity(
        id = id,
        login = login,
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
        twoFactorAuthentication = twoFactorAuthentication,
        orgLogin = orgLogin
    )
}

fun UserEntity.toUser(): User {
    return User(
        id = id,
        login = login,
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

fun Repository.toEntity(): RepositoryEntity {
    return RepositoryEntity(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        ownerId = owner.id,
        ownerLogin = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        isPrivate = private,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RepositoryEntity.toRepository(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        owner = User(
            id = ownerId,
            login = ownerLogin,
            nodeId = null, // Not available in RepositoryEntity
            avatarUrl = ownerAvatarUrl,
            gravatarId = null, // Not available in RepositoryEntity
            url = null, // Not available in RepositoryEntity
            htmlUrl = null, // Not available in RepositoryEntity
            followersUrl = null, // Not available in RepositoryEntity
            followingUrl = null, // Not available in RepositoryEntity
            gistsUrl = null, // Not available in RepositoryEntity
            starredUrl = null, // Not available in RepositoryEntity
            subscriptionsUrl = null, // Not available in RepositoryEntity
            organizationsUrl = null, // Not available in RepositoryEntity
            reposUrl = null, // Not available in RepositoryEntity
            eventsUrl = null, // Not available in RepositoryEntity
            receivedEventsUrl = null, // Not available in RepositoryEntity
            type = null, // Not available in RepositoryEntity
            siteAdmin = null, // Not available in RepositoryEntity
            name = null, // Not available in RepositoryEntity
            company = null, // Not available in RepositoryEntity
            blog = null, // Not available in RepositoryEntity
            location = null, // Not available in RepositoryEntity
            email = null, // Not available in RepositoryEntity
            starred = null, // Not available in RepositoryEntity
            bio = null, // Not available in RepositoryEntity
            publicRepos = null, // Not available in RepositoryEntity
            publicGists = null, // Not available in RepositoryEntity
            followers = null, // Not available in RepositoryEntity
            following = null, // Not available in RepositoryEntity
            createdAt = null, // Not available in RepositoryEntity
            updatedAt = null, // Not available in RepositoryEntity
            privateGists = null, // Not available in RepositoryEntity
            totalPrivateRepos = null, // Not available in RepositoryEntity
            ownedPrivateRepos = null, // Not available in RepositoryEntity
            diskUsage = null, // Not available in RepositoryEntity
            collaborators = null, // Not available in RepositoryEntity
            twoFactorAuthentication = null // Not available in RepositoryEntity
        ),
        private = isPrivate,
        htmlUrl = htmlUrl,
        language = language,
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        pushedAt = null // Not available in RepositoryEntity
    )
}

fun Event.toEntity(isReceivedEvent: Boolean, userLogin: String? = null): EventEntity {
    return EventEntity(
        id = id,
        type = type,
        actor = actor.toEntity(),
        repo = repo.let { eventRepo ->
            RepositoryEntity(
                id = eventRepo.id,
                name = eventRepo.name,
                fullName = eventRepo.name, // Assuming full name is the same as name for EventRepo
                description = null,
                ownerId = 0, // Default value, as owner info is not in EventRepo
                ownerLogin = "", // Default value
                ownerAvatarUrl = "", // Default value
                isPrivate = false, // Default value
                htmlUrl = eventRepo.url,
                language = null,
                stargazersCount = 0, // Default value
                watchersCount = 0, // Default value
                forksCount = 0, // Default value
                openIssuesCount = 0, // Default value
                createdAt = "", // Default value
                updatedAt = "" // Default value
            )
        },
        createdAt = createdAt,
        public = null,
        orgId = null,
        isReceivedEvent = isReceivedEvent,
        userLogin = userLogin
    )
}

fun EventEntity.toEvent(): Event {
    return Event(
        id = id,
        type = type ?: "", // Provide a default empty string if type is null
        actor = actor?.toUser() ?: User(
            login = "",
            id = 0,
            nodeId = null,
            avatarUrl = "",
            gravatarId = null,
            url = null,
            htmlUrl = null,
            followersUrl = null,
            followingUrl = null,
            gistsUrl = null,
            starredUrl = null,
            subscriptionsUrl = null,
            organizationsUrl = null,
            reposUrl = null,
            eventsUrl = null,
            receivedEventsUrl = null,
            type = null,
            siteAdmin = null,
            name = null,
            company = null,
            blog = null,
            location = null,
            email = null,
            starred = null,
            bio = null,
            publicRepos = null,
            publicGists = null,
            followers = null,
            following = null,
            createdAt = null,
            updatedAt = null,
            privateGists = null,
            totalPrivateRepos = null,
            ownedPrivateRepos = null,
            diskUsage = null,
            collaborators = null,
            twoFactorAuthentication = null
        ),
        repo = repo?.let { repositoryEntity ->
            EventRepo(
                id = repositoryEntity.id,
                name = repositoryEntity.name,
                url = repositoryEntity.htmlUrl
            )
        } ?: EventRepo(0, "", ""), // Provide a default empty EventRepo if repo is null
        payload = null,
        createdAt = createdAt ?: "" // Provide a default empty string if createdAt is null
    )
}





fun TrendingRepoModel.toTrendingEntity(): TrendingEntity {
    return TrendingEntity(
        fullName = fullName ?: "",
        url = url,
        description = description,
        language = language,
        meta = meta,
        contributors = contributors?.joinToString(","),
        contributorsUrl = contributorsUrl,
        starCount = starCount,
        forkCount = forkCount,
        name = name,
        reposName = reposName
    )
}

fun TrendingEntity.toTrendingRepoModel(): TrendingRepoModel {
    return TrendingRepoModel(
        fullName = fullName,
        url = url,
        description = description,
        language = language,
        meta = meta,
        contributors = contributors?.split(","),
        contributorsUrl = contributorsUrl,
        starCount = starCount,
        forkCount = forkCount,
        name = name,
        reposName = reposName
    )
}



