package com.shuyu.gsygithubappcompose.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuyu.gsygithubappcompose.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE login = :login")
    fun getUserByLogin(login: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE orgLogin = :orgLogin")
    fun getOrgMembers(orgLogin: String): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE orgLogin = :orgLogin")
    suspend fun clearOrgMembers(orgLogin: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
