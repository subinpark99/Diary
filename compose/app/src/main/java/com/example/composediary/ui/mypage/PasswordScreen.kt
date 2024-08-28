package com.example.composediary.ui.mypage


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.angry_diary.viewmodel.LockViewModel
import com.example.composediary.util.AppLockConst
import androidx.compose.runtime.*
import com.example.composediary.ui.theme.DarkBlue


@Composable
fun PasswordScreen(
    screenType: Int,
    navController: NavController,
    onUnlockSuccess: (Boolean) -> Unit, // 잠금 해제 성공 시 호출될 콜백
    lockViewModel: LockViewModel = hiltViewModel(),
) {

    var inputPassword by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var infoMessage by remember { mutableStateOf("비밀번호를 입력하세요") }
    var step by remember { mutableIntStateOf(1) } // 비밀번호 변경 단계

    // 비밀번호 입력 핸들러
    fun handlePasswordInput(number: String) {
        if (inputPassword.length < 4) {
            inputPassword += number
        }
        if (inputPassword.length == 4) {
            when (screenType) {
                AppLockConst.ENABLE_PASSLOCK -> {
                    lockViewModel.setPassword(inputPassword)
                    infoMessage = "비밀번호가 설정되었습니다."
                    navController.popBackStack()
                }

                AppLockConst.DISABLE_PASSLOCK -> {
                    if (lockViewModel.unlockPassword(inputPassword)) {
                        lockViewModel.removePassword()
                        infoMessage = "잠금 해제됨"
                        navController.popBackStack()
                    } else {
                        infoMessage = "비밀번호가 틀립니다"
                        inputPassword = ""
                    }
                }

                AppLockConst.UNLOCK_PASSWORD -> {
                    if (lockViewModel.unlockPassword(inputPassword)) {
                        infoMessage = "잠금 해제됨"
                        onUnlockSuccess(true) // 잠금 해제 성공 시 호출
                    } else {
                        infoMessage = "비밀번호가 틀립니다"
                        inputPassword = ""
                    }
                }

                AppLockConst.CHANGE_PASSWORD -> {
                    when (step) {
                        1 -> {
                            if (lockViewModel.unlockPassword(inputPassword)) {
                                infoMessage = "새로운 비밀번호를 입력하세요"
                                oldPassword = inputPassword
                                inputPassword = ""
                                step = 2
                            } else {
                                infoMessage = "기존 비밀번호가 틀립니다"
                                inputPassword = ""
                            }
                        }

                        2 -> {
                            oldPassword = inputPassword
                            infoMessage = "새로운 비밀번호를 다시 입력하세요"
                            inputPassword = ""
                            step = 3
                        }

                        3 -> {
                            if (oldPassword == inputPassword) {
                                lockViewModel.setPassword(inputPassword)
                                infoMessage = "비밀번호가 변경되었습니다."
                                navController.popBackStack()
                            } else {
                                infoMessage = "새로운 비밀번호가 일치하지 않습니다. 다시 시도하세요."
                                inputPassword = ""
                                step = 2
                            }
                        }
                    }
                }
            }
        }
    }

    // delete 버튼
    fun handleDelete() {
        if (inputPassword.isNotEmpty()) {
            inputPassword = inputPassword.dropLast(1)
        }
    }

    // clear 버튼
    fun handleClear() {
        inputPassword = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = infoMessage,
            fontSize = 28.sp,
            color = Color.Black
        )
        PutPassword(inputPassword = inputPassword)
        PasswordButton(
            onButtonClick = { handlePasswordInput(it) },
            onDeleteClick = { handleDelete() },
            onClearClick = { handleClear() }
        )
    }
}


@Composable
fun PutPassword(inputPassword: String) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        repeat(4) { index ->
            TextField(
                value = if (index < inputPassword.length) "•" else "",
                onValueChange = {},
                modifier = Modifier.size(width = 60.dp, height = 50.dp),
                readOnly = true,
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }
}

@Composable
fun PasswordButton(
    onButtonClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Column {
        (1..9).chunked(3).forEach { range ->
            PasswordRow(range.map { it.toString() }, onButtonClick)
        }
        PasswordRow(
            listOf("Clear", "0", "Delete"),
            onButtonClick = {
                when (it) {
                    "Clear" -> onClearClick()
                    "Delete" -> onDeleteClick()
                    else -> onButtonClick(it)
                }
            }
        )
    }
}

@Composable
fun PasswordRow(
    buttons: List<String>,
    onButtonClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        buttons.forEach { label ->
            Button(
                onClick = { onButtonClick(label) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            ) {
                Text(text = label, modifier = Modifier.padding(6.dp))
            }
        }
    }
}