package com.shuyu.gsygithubappcompose.data.repository

/**
 * A wrapper class for data returned from repositories, indicating the source of the data.
 *
 * @param T The type of the data.
 * @property result The actual data wrapped in a [kotlin.Result].
 * @property source The source of the data, either [DataSource.CACHE] or [DataSource.NETWORK].
 * @property isDbEmpty Indicates whether the database was empty when the initial query was made.
 */
data class RepositoryResult<T>(
    val result: kotlin.Result<T>,
    val source: DataSource,
    val isDbEmpty: Boolean = false
)

/**
 * Enum representing the source of the data.
 */
enum class DataSource {
    CACHE,
    NETWORK
}
