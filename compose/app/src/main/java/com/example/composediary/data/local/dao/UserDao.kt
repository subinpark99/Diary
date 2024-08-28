package com.example.composediary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composediary.data.local.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE tokenId = :tokenId")
    suspend fun getUserById(tokenId: String): User

    @Query("DELETE FROM user WHERE tokenId = :tokenId")  // 탈퇴 시
    suspend fun deleteAll(tokenId: String)
}

