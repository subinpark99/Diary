package com.example.composediary.ui.login


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.angry_diary.viewmodel.UserViewModel
import com.example.composediary.R
import com.example.composediary.ui.theme.DarkBlue
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, userViewModel: UserViewModel = hiltViewModel()) {

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // 이미지와 텍스트
        ) {
            Spacer(modifier = Modifier.height(200.dp))
            Image(
                painter = painterResource(id = R.drawable.angrylogo),
                contentDescription = "logo",
                Modifier.size(70.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
        }

        Button(
            onClick = { setupLoginButton(context, onLoginSuccess, userViewModel) },
            colors = ButtonColors(
                Color.Transparent, Color.Transparent, Color.Transparent,
                Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),

            ) {
            Image(
                painter = painterResource(id = R.drawable.icon_kakao_login),
                contentDescription = "login", modifier = Modifier.size(300.dp, 70.dp)
            )
        }
    }
}


fun setupLoginButton(context: Context, onLoginSuccess: () -> Unit, userViewModel: UserViewModel) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            // 로그인 실패
            Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
            Log.e("LoginError", error.message ?: "Unknown error")
        } else if (token != null) {
            // 로그인 성공
            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
            onLoginSuccess() // 로그인 성공 시 콜백 호출

            UserApiClient.instance.me { user, _ ->
                user?.let {
                    userViewModel.login(userId = user.id!!)
                }
            }
        }
    }

    // 실제 로그인 호출 (카카오톡이 설치되어 있는 경우 카카오톡으로 로그인, 그렇지 않으면 카카오 계정으로 로그인)
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}