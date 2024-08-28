package com.dev.angry_diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composediary.data.repository.LockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val lockRepository: LockRepository
) : ViewModel() {


    val password: StateFlow<String?> = lockRepository.currentPassword.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setPassword(password: String) {
        viewModelScope.launch {
            lockRepository.setPassword(password)
        }
    }

    fun removePassword() {
        viewModelScope.launch {
            lockRepository.removePassword()
        }
    }

    fun unlockPassword(password: String): Boolean {
        return lockRepository.unlockPassword(password)
    }
}
