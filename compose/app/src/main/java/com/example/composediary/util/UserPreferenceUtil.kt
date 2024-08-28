package com.example.composediary.util

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UserPreferenceUtil @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tokenId", 0)

    fun getTokenId(): String? = prefs.getString("tokenId", "")

    fun setTokenId(str: String) = prefs.edit().putString("tokenId", str).apply()

    fun setAutoLogin(boolean: Boolean) = prefs.edit().putBoolean("login", boolean).apply()

    fun getAutoLoginState() = prefs.getBoolean("login", false)
}
