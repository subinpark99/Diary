package com.dev.angry_diary.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composediary.data.repository.UserRepository
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    fun autoLoginState():Boolean{
        return userRepository.isLoggedIn()
    }

    fun login(userId:Long){
        userRepository.saveUserId(userId)
    }

    fun logout() {
        UserApiClient.instance.logout {
            viewModelScope.launch {
                userRepository.removeUserId()
            }
        }
    }

    fun withDraw() {
        UserApiClient.instance.logout {
            viewModelScope.launch {
                userRepository.removeUserId()
            }
        }
    }
}
