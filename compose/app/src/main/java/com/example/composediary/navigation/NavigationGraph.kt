package com.example.composediary.navigation


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.angry_diary.viewmodel.LockViewModel
import com.dev.angry_diary.viewmodel.UserViewModel
import com.example.composediary.ui.home.HomeScreen
import com.example.composediary.ui.login.LoginScreen
import com.example.composediary.ui.mypage.MyPageScreen
import com.example.composediary.ui.mypage.PasswordScreen
import com.example.composediary.util.AppLockConst

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
) {

    val lockViewModel: LockViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val password by lockViewModel.password.collectAsState()
    var unlocked by remember { mutableStateOf(false) }

    val startDestination = if (!userViewModel.autoLoginState()) {
        LOGIN // 로그인 상태가 아니면 로그인 화면으로 이동
    } else if (!password.isNullOrEmpty() && !unlocked) {
        PASSWORD // 로그인 상태인데 잠금이 설정되어 있고, 잠금이 해제되지 않은 경우
    } else {
        HOME // 로그인 상태이고 잠금이 해제되었거나 설정되지 않은 경우
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HOME) {
                        popUpTo(LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(HOME) {
            HomeScreen()
        }


        composable(MYPAGE) {
            MyPageScreen(navController, scaffoldState = scaffoldState)
        }

        composable(PASSWORD) {
            PasswordScreen(screenType = AppLockConst.UNLOCK_PASSWORD, navController,
                onUnlockSuccess = {
                    unlocked = it
                })
        }


        composable("$PASSWORD/{screenType}") { backStackEntry ->
            val screenTypeString = backStackEntry.arguments?.getString("screenType") ?: "0"
            val screenType = screenTypeString.toInt()

            PasswordScreen(screenType = screenType, navController,
                onUnlockSuccess = {
                    unlocked = it
                })

        }
    }
}

