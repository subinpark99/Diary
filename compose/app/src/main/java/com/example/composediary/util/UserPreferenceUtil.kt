package com.example.composediary.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject


class UserPreferenceUtil @Inject constructor(context: Context) {

    companion object {
        private const val PREF_NAME = "user_preferences"
        private const val USER_ID = "user_id"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setUserId(userId: Long) = prefs.edit().putLong(USER_ID, userId).apply()

    fun getUserId() = prefs.getLong(USER_ID, 0L)

    fun removeUserId() {
        prefs.edit().remove(USER_ID).apply()
    }

}

