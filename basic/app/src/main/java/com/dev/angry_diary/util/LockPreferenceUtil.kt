package com.dev.angry_diary.util;


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("state")

class LockPreferenceUtil(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val KEY_PASSWORD = stringPreferencesKey("password")
    }

    val password: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[KEY_PASSWORD]
        }

    // 비밀번호 설정
    suspend fun setPassword(password: String) {
        dataStore.edit { preferences ->
            preferences[KEY_PASSWORD] = password
        }
    }

    // 비밀번호 제거
    suspend fun removePassword() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_PASSWORD)
        }
    }
}
