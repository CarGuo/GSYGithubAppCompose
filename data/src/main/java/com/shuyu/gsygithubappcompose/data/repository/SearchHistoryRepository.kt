package com.shuyu.gsygithubappcompose.data.repository

import com.shuyu.gsygithubappcompose.core.database.dao.SearchHistoryDao
import com.shuyu.gsygithubappcompose.core.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) {

    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return searchHistoryDao.getSearchHistory()
    }

    suspend fun saveSearchQuery(query: String) {
        val searchHistoryEntity = SearchHistoryEntity(query = query, timestamp = System.currentTimeMillis())
        searchHistoryDao.insertSearchHistory(searchHistoryEntity)
        searchHistoryDao.deleteOldSearchHistory()
    }
}
