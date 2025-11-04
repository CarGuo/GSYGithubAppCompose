package com.shuyu.gsygithubappcompose.data.repository

/**
 * A wrapper class for data returned from repositories, indicating the source of the data.
 *
 * @param T The type of the data.
 * @property data The actual data wrapped in a [kotlin.Result].
 * @property dataSource The source of the data, either [DataSource.CACHE] or [DataSource.NETWORK].
 * @property isDbEmpty Indicates whether the database was empty when the initial query was made.
 */
data class RepositoryResult<T>(
    val data: kotlin.Result<T>,
    val dataSource: DataSource,
    val isDbEmpty: Boolean = false
)

/**
 * Enum representing the source of the data.
 */
enum class DataSource {
    CACHE,
    NETWORK
}
