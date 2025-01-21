package com.example.composediary.data.repository


import com.example.composediary.util.LockPreferenceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface LockRepository {
    val currentPassword: Flow<String?>

    suspend fun setPassword(password: String)
    suspend fun removePassword()
    fun unlockPassword(password: String): Boolean
}


class LockRepositoryImpl @Inject constructor(
    private val lockPreferenceUtil: LockPreferenceUtil
) : LockRepository {

    override val currentPassword: Flow<String?> = lockPreferenceUtil.password

    override suspend fun setPassword(password: String) {
        lockPreferenceUtil.setPassword(password)
    }

    override suspend fun removePassword() {
        lockPreferenceUtil.removePassword()
    }

    override fun unlockPassword(password: String): Boolean {
        return runBlocking {
            currentPassword.firstOrNull() == password
        }
    }
}

