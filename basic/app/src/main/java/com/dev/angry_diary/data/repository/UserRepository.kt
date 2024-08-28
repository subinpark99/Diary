package com.dev.angry_diary.data.repository

import com.dev.angry_diary.data.local.dao.UserDao
import com.dev.angry_diary.data.local.model.User
import com.dev.angry_diary.util.UserPreferenceUtil
import javax.inject.Inject

interface UserRepository {
    suspend fun insertUser(user: User)
    suspend fun getUserById(): User
    suspend fun deleteUser()
    suspend fun saveTokenId(tokenId: String)
    suspend fun setAutoLogin(boolean: Boolean)
    fun getAutoLoginState() : Boolean
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userPreferences: UserPreferenceUtil,
) : UserRepository {

    // 토큰 저장
    override suspend fun saveTokenId(tokenId: String) {
        userPreferences.setTokenId(tokenId)
    }

    override suspend fun setAutoLogin(boolean: Boolean) {
        userPreferences.setAutoLogin(boolean)
    }

    override fun getAutoLoginState() :Boolean {
        return userPreferences.getAutoLoginState()
    }

    // 토큰 조회
    private fun getUserTokenId(): String {
        return userPreferences.getTokenId() ?: "NoToken" // 기본값 설정
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    override suspend fun getUserById(): User = userDao.getUserById(getUserTokenId())


    override suspend fun deleteUser() = userDao.deleteAll(getUserTokenId())

}
