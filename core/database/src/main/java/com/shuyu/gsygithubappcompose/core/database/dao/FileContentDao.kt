package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuyu.gsygithubappcompose.core.database.entity.FileContentEntity

@Dao
interface FileContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fileContents: List<FileContentEntity>)

    @Query("SELECT * FROM file_content WHERE repoOwner = :repoOwner AND repoName = :repoName AND path = :path")
    suspend fun getFileContents(repoOwner: String, repoName: String, path: String): List<FileContentEntity>

    @Query("DELETE FROM file_content WHERE repoOwner = :repoOwner AND repoName = :repoName")
    suspend fun deleteAllFileContents(repoOwner: String, repoName: String)
}
