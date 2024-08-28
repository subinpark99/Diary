package com.dev.angry_diary.data.repository

import com.dev.angry_diary.util.LockPreferenceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface LockRepository {
    val currentPassword: StateFlow<String?>

    suspend fun setPassword(password: String)
    suspend fun removePassword()
    fun unlockPassword(password: String): Boolean
}


class LockRepositoryImpl @Inject constructor(
    private val lockPreferenceUtil: LockPreferenceUtil
) : LockRepository {

    override val currentPassword: StateFlow<String?> = lockPreferenceUtil.password
        .map { it }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    override suspend fun setPassword(password: String) {
        lockPreferenceUtil.setPassword(password)
    }

    override suspend fun removePassword() {
        lockPreferenceUtil.removePassword()
    }

    override fun unlockPassword(password: String): Boolean {
        return currentPassword.value == password
    }
}
