package com.dev.angry_diary.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject


class UserPreferenceUtil @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tokenId", 0)

    fun getTokenId(): String? = prefs.getString("tokenId", "")

    fun setTokenId(str: String) = prefs.edit().putString("tokenId", str).apply()

    fun setAutoLogin(boolean: Boolean) = prefs.edit().putBoolean("login", boolean).apply()

    fun getAutoLoginState() = prefs.getBoolean("login", false)
}
