package com.shuyu.gsygithubappcompose.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.shuyu.gsygithubappcompose.core.database.dao.HistoryDao
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.RepositoryDetailModel
import com.shuyu.gsygithubappcompose.data.repository.mapper.toHistoryEntity
import com.shuyu.gsygithubappcompose.data.repository.mapper.toRepositoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getHistoryList(): Flow<PagingData<Repository>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, enablePlaceholders = false
            ), pagingSourceFactory = { historyDao.getHistoryList() }).flow.map { pagingData ->
            pagingData.map { it.toRepositoryModel() }
        }
    }

    suspend fun saveHistory(repositoryDetailModel: RepositoryDetailModel) {
        historyDao.insert(repositoryDetailModel.toHistoryEntity())
    }
}
