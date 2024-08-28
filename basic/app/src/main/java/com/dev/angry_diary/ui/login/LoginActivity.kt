package com.dev.angry_diary.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dev.angry_diary.databinding.ActivityLoginBinding
import com.dev.angry_diary.data.local.model.User
import com.dev.angry_diary.ui.home.MainActivity
import com.dev.angry_diary.ui.viewmodel.UserViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val userViewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginStatus()
        setupLoginButton()
    }


    private fun checkLoginStatus() {    // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "로그인 기록 없음", Toast.LENGTH_SHORT).show()
            } else if (tokenInfo != null && userViewModel.autoLoginState.value) {
                navigateToMain()
            }
        }

    }

    private fun setupLoginButton() {   // 카카오 로그인 콜백

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                // 로그인 실패
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                Log.e("LoginError", error.message ?: "Unknown error")
            } else if (token != null) {
                // 로그인 성공
                saveUser(token.accessToken)
                navigateToMain()
            }
        }

        binding.kakaoLogin.setOnClickListener { // 로그인 버튼 클릭
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    private fun saveUser(token:String) {
        UserApiClient.instance.accessTokenInfo { tokenInfo, _ ->
            if (tokenInfo != null) {
                UserApiClient.instance.me { user, _ ->
                    user?.let {
                        val userName = it.kakaoAccount?.profile?.nickname ?: ""
                        val userEmail = it.kakaoAccount?.email ?: ""

                        val userData = User(userEmail,userName, token)
                        userViewModel.addUser(userData)
                    }
                }
            }
        }
    }

    private fun navigateToMain() {  // 메인으로 이동
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

}

