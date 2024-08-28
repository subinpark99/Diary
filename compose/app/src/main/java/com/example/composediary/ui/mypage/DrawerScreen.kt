package com.example.composediary.ui.mypage

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.angry_diary.viewmodel.DiaryViewModel
import com.dev.angry_diary.viewmodel.LockViewModel
import com.dev.angry_diary.viewmodel.UserViewModel
import com.example.composediary.R
import com.example.composediary.navigation.LOGIN
import com.example.composediary.navigation.PASSWORD
import com.example.composediary.ui.theme.LittleGreen
import com.example.composediary.ui.theme.RedWhite
import com.example.composediary.util.AppLockConst


@Composable
fun drawerMenu(): List<String> {
    return listOf(
        stringResource(id = R.string.lock),
        stringResource(id = R.string.unlock),
        stringResource(id = R.string.change_pw),
        stringResource(id = R.string.logout),
        stringResource(id = R.string.withdraw),
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DrawerScreen(
    navController: NavController,
    diaryViewModel: DiaryViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    lockViewModel: LockViewModel = hiltViewModel(),
) {
    val menuItems = drawerMenu()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = RedWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = LittleGreen)
                .padding(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "설정 아이콘",
                modifier = Modifier.size(50.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Setting",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val password by lockViewModel.password.collectAsState()
        val context = LocalContext.current

        menuItems.forEach { menu ->
            DrawerItem(menu = menu, click = {
                when (menu) {
                    menuItems[0] -> {
                        if (!password.isNullOrEmpty()) Toast.makeText(
                            context,
                            "비밀번호가 이미 존재합니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        else navigateItem(navController, AppLockConst.ENABLE_PASSLOCK)
                    }

                    menuItems[1] -> {
                        if (password.isNullOrEmpty()) Toast.makeText(
                            context,
                            "비밀번호가 존재하지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        else navigateItem(navController, AppLockConst.DISABLE_PASSLOCK)
                    }

                    menuItems[2] -> {
                        if (password.isNullOrEmpty()) Toast.makeText(
                            context,
                            "비밀번호가 존재하지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        else navigateItem(navController, AppLockConst.CHANGE_PASSWORD)
                    }

                    menuItems[3] -> {
                        userViewModel.logout()
                        lockViewModel.removePassword()

                        navController.navigate(LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }

                    }

                    menuItems[4] -> {
                        userViewModel.withDraw()
                        diaryViewModel.deleteAllDiaries()
                        lockViewModel.removePassword()

                        navController.navigate(LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            })
        }
    }
}

fun navigateItem(navController: NavController, type: Int) {
    navController.navigate("password/$type") {
        popUpTo(PASSWORD) { inclusive = true }
    }
}

@Composable
fun DrawerItem(menu: String, click: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable { click() }
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 10.dp)
            .background(color = RedWhite)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(color = RedWhite)
        ) {

            Spacer(modifier = Modifier.width(16.dp))
            Text(text = menu, fontSize = 20.sp, color = Color.Black)
        }
    }
}


