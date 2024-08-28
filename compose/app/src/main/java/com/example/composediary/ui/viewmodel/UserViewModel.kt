package com.dev.angry_diary.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composediary.data.local.model.User
import com.example.composediary.data.repository.UserRepository
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {


    private val _tokenId = MutableStateFlow(0L)
    val tokenId: StateFlow<Long> get() = _tokenId

    // 사용자 상태를 관리하기 위한 StateFlow
    private val _userState = MutableStateFlow(User())
    val userState: StateFlow<User> = _userState.asStateFlow()

    val autoLoginState = MutableStateFlow(userRepository.getAutoLoginState())

    init {
        loadUser()  // 초기 사용자 데이터 로드
    }

    fun loadUser() {
        viewModelScope.launch {
            val user = userRepository.getUserById()
            _userState.value = user
        }
    }

    // 사용자 추가
    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
            userRepository.saveTokenId(user.tokenId)
            userRepository.setAutoLogin(true)
        }
    }

    fun logout() {
        UserApiClient.instance.logout {
            viewModelScope.launch {
                userRepository.setAutoLogin(false)
            }
        }
    }

    fun withDraw() {
        UserApiClient.instance.logout {
            viewModelScope.launch {
                userRepository.setAutoLogin(false)
                userRepository.deleteUser()
            }
        }
    }
}
