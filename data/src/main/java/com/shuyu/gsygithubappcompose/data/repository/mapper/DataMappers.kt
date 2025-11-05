package com.shuyu.gsygithubappcompose.data.repository.mapper

import com.shuyu.gsygithubappcompose.core.database.entity.CommitEntity
import com.shuyu.gsygithubappcompose.core.database.entity.CommitUserEntity
import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity
import com.shuyu.gsygithubappcompose.core.database.entity.FileContentEntity
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryDetailEntity
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.database.entity.TrendingEntity
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import com.shuyu.gsygithubappcompose.core.network.graphql.GetRepositoryDetailQuery
import com.shuyu.gsygithubappcompose.core.network.model.CommitDetail
import com.shuyu.gsygithubappcompose.core.network.model.CommitUser
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.EventRepo
import com.shuyu.gsygithubappcompose.core.network.model.FileContent
import com.shuyu.gsygithubappcompose.core.network.model.RepoCommit
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.RepositoryDetailModel
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

fun GetRepositoryDetailQuery.Repository.toEntity(): RepositoryDetailEntity {
    val fields = this.comparisonFields
    return RepositoryDetailEntity(
        id = fields.id,
        name = fields.name,
        owner = fields.owner.login,
        ownerAvatarUrl = fields.owner.avatarUrl as String?,
        license = fields.licenseInfo?.name,
        forkCount = fields.forkCount,
        stargazersCount = fields.stargazers.totalCount,
        watchersCount = fields.watchers.totalCount,
        hasIssuesEnabled = fields.hasIssuesEnabled,
        viewerHasStarred = fields.viewerHasStarred,
        viewerSubscription = fields.viewerSubscription?.name,
        defaultBranchRef = fields.defaultBranchRef?.name,
        isFork = fields.isFork,
        size = fields.languages?.totalSize,
        languages = fields.languages?.nodes?.joinToString(", ") { it?.name.toString() },
        createdAt = fields.createdAt as String,
        pushedAt = fields.pushedAt as String?,
        sshUrl = fields.sshUrl as String?,
        url = fields.url as String?,
        shortDescriptionHTML = fields.shortDescriptionHTML as String?,
        topics = fields.repositoryTopics.nodes?.joinToString(", ") { it?.topic?.name.toString() },
        issuesClosed = fields.issuesClosed.totalCount,
        issuesOpen = fields.issuesOpen.totalCount,
        issuesTotal = fields.issues.totalCount,
        nameWithOwner = fields.nameWithOwner,
        parentNameWithOwner = this.parent?.comparisonFields?.nameWithOwner,
        parentFullName = this.parent?.comparisonFields?.nameWithOwner
    )
}


fun RepositoryDetailEntity.toRepositoryDetailModel(): RepositoryDetailModel {
    return RepositoryDetailModel(
        id = this.id,
        name = this.name,
        owner = this.owner,
        ownerAvatarUrl = this.ownerAvatarUrl,
        license = this.license,
        forkCount = this.forkCount,
        stargazersCount = this.stargazersCount,
        watchersCount = this.watchersCount,
        hasIssuesEnabled = this.hasIssuesEnabled,
        viewerHasStarred = this.viewerHasStarred,
        viewerSubscription = this.viewerSubscription,
        defaultBranchRef = this.defaultBranchRef,
        isFork = this.isFork,
        languages = this.languages?.split(", "),
        createdAt = this.createdAt,
        pushedAt = this.pushedAt,
        sshUrl = this.sshUrl,
        url = this.url,
        shortDescriptionHTML = this.shortDescriptionHTML,
        topics = this.topics?.split(", "),
        issuesClosed = this.issuesClosed,
        issuesOpen = this.issuesOpen,
        issuesTotal = this.issuesTotal,
        nameWithOwner = this.nameWithOwner,
        parentNameWithOwner = this.parentNameWithOwner,
        parentFullName = this.parentFullName,
        size = size,
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
            twoFactorAuthentication = null, // Not available in RepositoryEntity
            followingUrl = null,// Not available in RepositoryEntity
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

fun Event.toEntity(
    isReceivedEvent: Boolean,
    userLogin: String? = null,
    repoOwnerLogin: String? = null,
    repoName: String? = null
): EventEntity {
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
        userLogin = userLogin,
        repoOwnerLogin = repoOwnerLogin ?: "",
        repoFullName = repoName ?: "",
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
            twoFactorAuthentication = null,
            followingUrl = null
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

fun RepoCommit.toEntity(repoOwnerLogin: String, repoName: String): CommitEntity {
    return CommitEntity(
        sha = sha,
        message = commit.message,
        author = commit.author?.toCommitUserEntity(),
        committer = commit.committer?.toCommitUserEntity(),
        repoOwnerLogin = repoOwnerLogin,
        repoName = repoName
    )
}

fun CommitUser.toCommitUserEntity(): CommitUserEntity {
    return CommitUserEntity(
        name = name, email = email, date = date
    )
}

fun CommitEntity.toRepoCommit(): RepoCommit {
    return RepoCommit(
        sha = sha,
        nodeId = null, // Not stored in entity
        commit = CommitDetail(
            author = author?.toNetworkCommitAuthor(),
            committer = committer?.toNetworkCommitAuthor(),
            message = message ?: "",
            tree = null, // Not stored in entity
            url = null, // Not stored in entity
            commentCount = null, // Not stored in entity
            verification = null // Not stored in entity
        ),
        url = null, // Not stored in entity
        htmlUrl = null, // Not stored in entity
        commentsUrl = null, // Not stored in entity
        author = null, // Not enough info in CommitUserEntity to create a full User object
        committer = null, // Not enough info in CommitUserEntity to create a full User object
        parents = null // Not stored in entity
    )
}

fun CommitUserEntity.toNetworkCommitAuthor(): CommitUser {
    return CommitUser(
        name = name, email = email, date = date
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

fun FileContent.toFileContentEntity(repoOwner: String, repoName: String): FileContentEntity {
    return FileContentEntity(
        repoOwner = repoOwner,
        repoName = repoName,
        type = type,
        name = name,
        path = path,
        sha = sha,
        url = url,
        gitUrl = gitUrl,
        htmlUrl = htmlUrl,
        downloadUrl = downloadUrl
    )
}

fun FileContentEntity.toFileContent(): FileContent {
    return FileContent(
        type = type,
        encoding = null, // Not stored in entity
        size = 0, // Not stored in entity
        name = name,
        path = path,
        content = null, // Not stored in entity
        sha = sha,
        url = url,
        gitUrl = gitUrl,
        htmlUrl = htmlUrl,
        downloadUrl = downloadUrl,
        links = null // Not stored in entity
    )
}
