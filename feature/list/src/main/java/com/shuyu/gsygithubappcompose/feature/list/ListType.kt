package com.shuyu.gsygithubappcompose.feature.list

enum class CommonListDataType(val value: String) {
    FOLLOWER("follower"),
    FOLLOWED("followed"),
    USER_REPOS("user_repos"),
    REPO_STAR("repo_star"),
    USER_STAR("user_star"),
    REPO_WATCHER("repo_watcher"),
    REPO_FORK("repo_fork"),
    REPO_RELEASE("repoRelease"),
    REPO_TAG("repo_tag"),
    NOTIFY("notify"),
    TOPICS("topics"),
    USER_BE_STARED("user_be_stared"),
    USER_ORGS("user_orgs");
}
