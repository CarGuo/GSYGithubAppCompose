package com.shuyu.gsygithubappcompose.core.network.model

import com.google.gson.annotations.SerializedName

data class GitHubError(
    val message: String,
    @SerializedName("documentation_url")
    val documentationUrl: String?,
    val errors: List<ErrorDetail>?
)

data class ErrorDetail(
    val resource: String?,
    val field: String?,
    val code: String?,
    val message: String?
)
