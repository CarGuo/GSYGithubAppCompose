package com.shuyu.gsygithubappcompose.feature.list

enum class CommonListDataType(val value: String) {
    REPOSITORIES("repositories"),
    FOLLOWER("follower"),
    FOLLOWING("following"),
    FOLLOWED("followed"),
    USER_REPOS("user_repos"),
    STARGAZERS("stargazers"),
    REPO_STAR("repo_star"),
    USER_STAR("user_star"),
    WATCHERS("watchers"),
    REPO_WATCHER("repo_watcher"),
    FORKS("forks"),
    REPO_FORK("repo_fork"),
    REPO_RELEASE("repoRelease"),
    REPO_TAG("repo_tag"),
    NOTIFY("notify"),
    TOPICS("topics"),
    USER_BE_STARED("user_be_stared"),
    USER_ORGS("user_orgs");
}
