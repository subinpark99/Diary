package com.example.composediary.data.repository

import com.example.composediary.util.UserPreferenceUtil
import javax.inject.Inject

interface UserRepository {
    fun saveUserId(userId: Long)
    fun getUserId(): Long
    fun removeUserId()
    fun isLoggedIn(): Boolean
}

class UserRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferenceUtil,
) : UserRepository {


    override fun saveUserId(userId: Long) {  // 로그인 시 저장
      userPreferences.setUserId(userId)
    }

    override fun getUserId(): Long {
        return userPreferences.getUserId()
    }

    override fun removeUserId() {  // 로그아웃, 회원탈퇴 시
        return userPreferences.removeUserId()
    }

    // 사용자 ID가 존재하는지 확인 (로그인 상태 확인)
    override fun isLoggedIn(): Boolean {
        val userId = getUserId()
        return userId!=0L
    }
}