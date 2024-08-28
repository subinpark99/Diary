package com.example.composediary.ui.mypage


import android.os.Build
import androidx.compose.runtime.*
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalDrawer
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.angry_diary.viewmodel.DiaryViewModel
import com.dev.angry_diary.viewmodel.UserViewModel
import com.example.composediary.R
import com.example.composediary.data.local.model.DiaryMonthlyCount
import com.example.composediary.ui.theme.Blue
import com.example.composediary.ui.theme.DarkBlue
import com.example.composediary.ui.theme.DarkGreen
import com.example.composediary.ui.theme.Gray
import com.example.composediary.ui.theme.Green
import com.example.composediary.ui.theme.LightBlue
import com.example.composediary.ui.theme.LightOrange
import com.example.composediary.ui.theme.LightPink
import com.example.composediary.ui.theme.LittleGreen
import com.example.composediary.ui.theme.Orange
import com.example.composediary.ui.theme.Red
import com.example.composediary.ui.theme.Yellow
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageScreen(
    navController: NavController,
    diaryViewModel: DiaryViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState,
) {
    val scope = rememberCoroutineScope()

    val user by userViewModel.userState.collectAsState()
    val diaryCounts by diaryViewModel.diariesCountState.collectAsState()
    val monthlyCount by diaryViewModel.monthlyDiaryCountState.collectAsState()

    val colorList = listOf(
        Orange,
        Gray,
        DarkBlue,
        LittleGreen,
        Yellow,
        Green,
        LightOrange,
        LightPink,
        LightBlue,
        Blue,
        DarkGreen,
        Red
    )

    ModalDrawer(
        drawerContent = { DrawerScreen(navController = navController) },
        drawerState = scaffoldState.drawerState
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.open() // 버튼 클릭 시 Drawer 열기
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "drawerIcon",
                        modifier = Modifier.size(35.dp),
                    )
                }

                Text(
                    text = stringResource(id = R.string.my_info),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .weight(1f) // Row 내에서 텍스트를 가운데로 확장
                        .wrapContentWidth(Alignment.CenterHorizontally) // 텍스트를 중앙 정렬
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
            Column(
                modifier = Modifier
                    .border(border = BorderStroke(1.dp, Gray), shape = RoundedCornerShape(10.dp))
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MyInfo(R.string.nickname, user.userName)
                MyInfo(R.string.diary_count, "$diaryCounts 개")
                Text(text = stringResource(id = R.string.monthly_count), fontSize = 20.sp)
                MonthlyDiaryGraph(
                    colors = colorList,
                    data = monthlyCount,
                    graphHeight = 240
                )
                MonthlyDiaryGrid(data = monthlyCount, colors = colorList)
            }
        }
    }
}


@Composable
fun MyInfo(info: Int, data: String) {

    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(text = stringResource(id = info), fontSize = 20.sp)
        Text(text = data, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterVertically))
    }
}


@Composable
internal fun MonthlyDiaryGraph(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    data: List<DiaryMonthlyCount>,
    graphHeight: Int,
) {
    val total = data.sumOf { it.count }
    val angles = data.map { it.count.toFloat() / total * 360f }

    Canvas(
        modifier = modifier
            .height(graphHeight.dp)
            .fillMaxSize()
    ) {

        val strokeWidth = graphHeight.dp.toPx() / 4
        val radius = (graphHeight.dp.toPx() - strokeWidth) / 2
        val centerX = size.width / 2f
        val centerY = radius + strokeWidth / 2

        drawGraph(angles, colors, radius, strokeWidth, centerX, centerY)
    }
}

private fun DrawScope.drawGraph(
    angles: List<Float>,
    colors: List<Color>,
    radius: Float,
    strokeWidth: Float,
    centerX: Float,
    centerY: Float,
) {
    var startAngle = -90f

    angles.forEachIndexed { index, angle ->
        val color = colors[index]

        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = angle,
            useCenter = false,
            style = Stroke(width = strokeWidth),
            topLeft = Offset(centerX - radius, centerY - radius),
            size = Size(radius * 2, radius * 2)
        )

        startAngle += angle
    }
}

@Composable
fun MonthlyDiaryGrid(
    data: List<DiaryMonthlyCount>,
    colors: List<Color>,
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(3.dp)
    ) {
        items(data.size) { index ->

            val month = "${data[index].month} 월"
            val count = data.getOrNull(index)?.count ?: 0f
            val color = colors[index % colors.size]

            GridItem(month = month, count = count.toInt(), color = color)
        }
    }
}

@Composable
fun GridItem(month: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = month,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )

        Text(
            text = "$count 개",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
