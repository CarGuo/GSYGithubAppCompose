package com.shuyu.gsygithubappcompose.data.repository.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shuyu.gsygithubappcompose.core.database.entity.CommitEntity
import com.shuyu.gsygithubappcompose.core.database.entity.CommitDetailEntity
import com.shuyu.gsygithubappcompose.core.database.entity.CommitFileEntity
import com.shuyu.gsygithubappcompose.core.database.entity.CommitStatsEntity
import com.shuyu.gsygithubappcompose.core.database.entity.CommitTreeEntity
import com.shuyu.gsygithubappcompose.core.database.entity.CommitUserEntity
import com.shuyu.gsygithubappcompose.core.database.entity.EventEntity
import com.shuyu.gsygithubappcompose.core.database.entity.FileContentEntity
import com.shuyu.gsygithubappcompose.core.database.entity.HistoryEntity
import com.shuyu.gsygithubappcompose.core.database.entity.IssueCommentEntity
import com.shuyu.gsygithubappcompose.core.database.entity.IssueEntity
import com.shuyu.gsygithubappcompose.core.database.entity.PushCommitEntity
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryDetailEntity
import com.shuyu.gsygithubappcompose.core.database.entity.RepositoryEntity
import com.shuyu.gsygithubappcompose.core.database.entity.TrendingEntity
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import com.shuyu.gsygithubappcompose.core.network.graphql.GetRepositoryDetailQuery
import com.shuyu.gsygithubappcompose.core.network.model.Comment
import com.shuyu.gsygithubappcompose.core.network.model.CommitDetail
import com.shuyu.gsygithubappcompose.core.network.model.CommitFile
import com.shuyu.gsygithubappcompose.core.network.model.CommitStats
import com.shuyu.gsygithubappcompose.core.network.model.CommitTree
import com.shuyu.gsygithubappcompose.core.network.model.CommitUser
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.network.model.EventRepo
import com.shuyu.gsygithubappcompose.core.network.model.FileContent
import com.shuyu.gsygithubappcompose.core.network.model.Issue
import com.shuyu.gsygithubappcompose.core.network.model.IssueLabel
import com.shuyu.gsygithubappcompose.core.network.model.Organization
import com.shuyu.gsygithubappcompose.core.network.model.PushCommit
import com.shuyu.gsygithubappcompose.core.network.model.RepoCommit
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.RepositoryDetailModel
import com.shuyu.gsygithubappcompose.core.network.model.TrendingRepoModel
import com.shuyu.gsygithubappcompose.core.network.model.User
import java.util.Date


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
    val gson = Gson()
    return CommitEntity(
        sha = sha,
        message = commit.message,
        author = gson.toJson(author),
        committer = gson.toJson(committer),
        repoOwnerLogin = repoOwnerLogin,
        repoName = repoName,
        commitDetail = gson.toJson(commit)
    )
}

fun CommitUser.toCommitUserEntity(): CommitUserEntity {
    return CommitUserEntity(
        name = name, email = email, date = date
    )
}

fun CommitUserEntity.toCommitUser(): CommitUser {
    return CommitUser(
        name = name, email = email, date = date
    )
}


fun CommitEntity.toRepoCommit(): RepoCommit {
    val gson = Gson()
    val authorUser = author?.let { gson.fromJson(it, User::class.java) }
    val committerUser = committer?.let { gson.fromJson(it, User::class.java) }
    return RepoCommit(
        sha = sha,
        nodeId = null, // Not stored in entity
        commit = gson.fromJson(commitDetail, CommitDetail::class.java),
        url = null, // Not stored in entity
        htmlUrl = null, // Not stored in entity
        commentsUrl = null, // Not stored in entity
        author = authorUser,
        committer = committerUser,
        parents = null // Not stored in entity
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

fun Issue.toIssueEntity(owner: String, repoName: String): IssueEntity {
    return IssueEntity(
        id = id,
        nodeId = nodeId,
        number = number,
        title = title,
        user = user?.toEntity(),
        labels = Gson().toJson(labels), // Convert list of labels to JSON string
        state = state,
        locked = locked,
        assignee = assignee?.toEntity(),
        comments = comments,
        createdAt = createdAt,
        updatedAt = updatedAt,
        closedAt = closedAt,
        body = body,
        bodyHtml = bodyHtml,
        htmlUrl = htmlUrl,
        repositoryUrl = repositoryUrl,
        owner = owner,
        repoName = repoName,
    )
}

fun IssueEntity.toIssue(): Issue {
    val gson = Gson()
    return Issue(
        id = id,
        nodeId = nodeId,
        number = number,
        title = title,
        user = user?.toUser(),
        labels = labels?.let {
            gson.fromJson(
                it, object : TypeToken<List<IssueLabel>>() {}.type
            )
        } ?: emptyList(), // Convert JSON string to list of labels
        state = state,
        locked = locked,
        assignee = assignee?.toUser(),
        assignees = null, // Not stored in entity
        comments = comments,
        createdAt = createdAt,
        updatedAt = updatedAt,
        closedAt = closedAt,
        body = body,
        bodyHtml = bodyHtml,
        htmlUrl = htmlUrl,
        repositoryUrl = repositoryUrl)
}

fun Comment.toIssueCommentEntity(issueId: Long): IssueCommentEntity {
    return IssueCommentEntity(
        id = id,
        issueId = issueId,
        nodeId = nodeId,
        url = url,
        htmlUrl = htmlUrl,
        issueUrl = null, // Not available in Comment model
        body = body,
        bodyHtml = bodyHtml,
        bodyText = bodyText,
        userLogin = user.login,
        userAvatarUrl = user.avatarUrl,
        userId = user.id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        authorAssociation = authorAssociation
    )
}

fun IssueCommentEntity.toIssueComment(): Comment {
    return Comment(
        id = id,
        nodeId = nodeId,
        url = url,
        htmlUrl = htmlUrl,
        body = body,
        bodyHtml = bodyHtml,
        bodyText = bodyText,
        user = User.toMiniUserModel(
            login = userLogin, avatarUrl = userAvatarUrl ?: "", id = userId ?: 0
        ), // Simplified User creation
        createdAt = createdAt,
        updatedAt = updatedAt,
        authorAssociation = authorAssociation
    )
}

fun CommitStats.toEntity(): CommitStatsEntity {
    return CommitStatsEntity(
        total = total, additions = additions, deletions = deletions
    )
}

fun CommitStatsEntity.toModel(): CommitStats {
    return CommitStats(
        total = total, additions = additions, deletions = deletions
    )
}

fun CommitFile.toEntity(): CommitFileEntity {
    return CommitFileEntity(
        sha = sha,
        filename = filename,
        status = status,
        additions = additions,
        deletions = deletions,
        changes = changes,
        blobUrl = blobUrl,
        rawUrl = rawUrl,
        contentsUrl = contentsUrl,
        patch = patch
    )
}

fun CommitFileEntity.toModel(): CommitFile {
    return CommitFile(
        sha = sha,
        filename = filename,
        status = status,
        additions = additions,
        deletions = deletions,
        changes = changes,
        blobUrl = blobUrl,
        rawUrl = rawUrl,
        contentsUrl = contentsUrl,
        patch = patch
    )
}

fun CommitTree.toEntity(): CommitTreeEntity {
    return CommitTreeEntity(
        sha = sha, url = url
    )
}

fun CommitTreeEntity.toModel(): CommitTree {
    return CommitTree(
        sha = sha, url = url
    )
}

fun CommitDetail.toEntity(): CommitDetailEntity {
    return CommitDetailEntity(
        author = author?.toCommitUserEntity(),
        committer = committer?.toCommitUserEntity(),
        message = message,
        tree = tree?.toEntity(),
        url = url,
        commentCount = commentCount
    )
}

fun CommitDetailEntity.toModel(): CommitDetail {
    return CommitDetail(
        author = author?.toCommitUser(),
        committer = committer?.toCommitUser(),
        message = message,
        tree = tree?.toModel(),
        url = url,
        commentCount = commentCount,
        verification = null // Not stored in entity
    )
}

fun PushCommit.toEntity(): PushCommitEntity {
    return PushCommitEntity(
        sha = sha ?: "",
        url = url,
        htmlUrl = htmlUrl,
        commentsUrl = commentsUrl,
        stats = stats?.toEntity(),
        commit = commit?.toEntity(),
        author = author?.toEntity(),
        committer = committer?.toEntity(),
        files = files?.map { it.toEntity() })
}

fun PushCommitEntity.toModel(): PushCommit {
    return PushCommit(
        sha = sha,
        url = url,
        htmlUrl = htmlUrl,
        commentsUrl = commentsUrl,
        stats = stats?.toModel(),
        commit = commit?.toModel(),
        author = author?.toUser(),
        committer = committer?.toUser(),
        files = files?.map { it.toModel() },
        parents = null // This is not stored in the database
    )
}

fun Organization.toUser(): User {
    return User(
        login = login,
        id = id,
        nodeId = nodeId,
        avatarUrl = avatarUrl ?:"",
        gravatarId = null,
        url = url,
        htmlUrl = null,
        followersUrl = null,
        followingUrl = null,
        gistsUrl = null,
        starredUrl = null,
        subscriptionsUrl = null,
        organizationsUrl = null,
        reposUrl = reposUrl,
        eventsUrl = null,
        receivedEventsUrl = null,
        type = "Organization",
        siteAdmin = false,
        name = null,
        company = null,
        blog = null,
        location = null,
        email = null,
        starred = null,
        bio = description,
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
        twoFactorAuthentication = false
    )
}

fun RepositoryDetailModel.toHistoryEntity(): HistoryEntity {
    return HistoryEntity(
        id = id,
        fullName = nameWithOwner,
        name = name,
        owner = owner,
        ownerName = owner,
        description = shortDescriptionHTML,
        language = languages?.joinToString(),
        starCount = stargazersCount,
        forkCount = forkCount,
        watcherCount = watchersCount,
        openIssuesCount = issuesOpen,
        subscribersCount = 0,
        pushAt = Date(),
        createAt = Date(),
        updateAt = Date(),
        license = license,
        fork = isFork,
        topics = topics,
        data = Gson().toJson(this),
        insertDate = Date().time
    )
}

fun HistoryEntity.toRepositoryModel(): Repository {
    return Gson().fromJson(data, Repository::class.java)
}
