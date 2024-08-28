package com.example.composediary.data.repository

import com.example.composediary.data.local.dao.UserDao
import com.example.composediary.data.local.model.User
import com.example.composediary.util.UserPreferenceUtil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
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