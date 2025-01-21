package com.example.composediary

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composediary.data.local.model.Diary
import com.example.composediary.navigation.BottomNavigationBar
import com.example.composediary.navigation.HOME
import com.example.composediary.navigation.MYPAGE
import com.example.composediary.navigation.NavigationGraph
import com.example.composediary.ui.home.AddDiaryDialog
import com.example.composediary.ui.theme.Orange
import com.example.composediary.ui.viewmodel.DiaryViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    diaryViewModel: DiaryViewModel = hiltViewModel(),
) {

    val navController = rememberNavController()
    var isDialogOpen by remember { mutableStateOf(false) }
    var diaryText by remember { mutableStateOf("") }

    val navState = navController.currentBackStackEntryAsState().value?.destination?.route

    val scaffold = rememberScaffoldState()  // ScaffoldState 생성
    val drawerState = scaffold.drawerState.currentValue


    Scaffold(
        scaffoldState = scaffold,
        bottomBar = {
            if (navState == HOME || navState == MYPAGE && drawerState.toString() != "Open") {
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.background
                ) {
                    BottomNavigationBar(
                        navController = navController
                    )
                }
            }
        },

        floatingActionButton = {
            if (navState == HOME || navState == MYPAGE && drawerState.toString() != "Open") {
                FloatingActionButton(
                    onClick = { isDialogOpen = true },
                    contentColor = Color.Black,
                    backgroundColor = Orange,
                    modifier = Modifier.size(70.dp)

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_add),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    )
    { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController, scaffold)
        }
    }
    if (isDialogOpen) {
        AddDiaryDialog(
            diaryText = diaryText,
            onValueChanged = { diaryText = it },
            onDismissRequest = { isDialogOpen = false },
            onSave = {
                addDiary(diaryText, diaryViewModel)
                isDialogOpen = false  // 다이얼로그 닫기
                diaryText = ""
            })
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun addDiary(content: String, diaryViewModel: DiaryViewModel) {

    // 현재 날짜와 시간 가져오기
    val now = LocalDateTime.now()

    // 날짜와 시간 포맷 정의
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // 날짜와 시간을 문자열로 변형
    val date = now.format(dateFormatter)
    val time = now.format(timeFormatter)

    val diary = Diary(contentId = 0, content = content, date, time)
    diaryViewModel.addDiary(diary)
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    MainScreen(

    )
}