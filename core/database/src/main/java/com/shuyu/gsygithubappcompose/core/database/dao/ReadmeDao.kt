package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuyu.gsygithubappcompose.core.database.entity.ReadmeEntity

@Dao
interface ReadmeDao {

    @Query("SELECT * FROM readme WHERE owner = :owner AND repo = :repo")
    fun getReadme(owner: String, repo: String): ReadmeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(readme: ReadmeEntity)

    @Query("DELETE FROM readme WHERE owner = :owner AND repo = :repo")
    suspend fun delete(owner: String, repo: String)
}
