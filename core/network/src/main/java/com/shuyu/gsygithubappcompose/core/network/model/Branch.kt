package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class Branch(
    val name: String,
    val commit: BranchCommit,
    val protected: Boolean?
)

data class BranchCommit(
    val sha: String,
    val url: String?
)
