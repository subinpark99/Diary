package com.example.composediary.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.angry_diary.viewmodel.DiaryViewModel
import com.example.composediary.R
import com.example.composediary.data.local.model.Diary
import com.example.composediary.ui.theme.DarkBlue
import com.example.composediary.ui.theme.Gray
import com.example.composediary.ui.theme.RedWhite
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.*

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun HomeScreen(
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    val diaries by diaryViewModel.diariesState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(7.dp)
    ) {

        DateHeaderView(diaryViewModel)

        // 날짜별로 내용을 그룹화
        val groupedDiaries = diaries.groupBy { it.date }

        LazyColumn {
            items(groupedDiaries.toList()) { (date, diary) ->
                DateItemView(date = date, diary)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeaderView(diaryViewModel: DiaryViewModel) {  // 연월 표시, 이전/다음달 이동 화살표

    val currentMonth by diaryViewModel.currentMonthState.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = {
            diaryViewModel.goToPreviousMonth()
        }) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                tint = DarkBlue,
                modifier = Modifier.size(60.dp),
                contentDescription = "이전 달"
            )
        }

        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")),
            fontSize = 25.sp,
            color = DarkBlue,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        IconButton(onClick = {
            diaryViewModel.goToNextMonth()
        }) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                tint = DarkBlue,
                modifier = Modifier.size(60.dp),
                contentDescription = "다음 달"
            )
        }
    }

}

@Composable
fun DateItemView(date: String, diaries: List<Diary>) {   // 날짜 아이템
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_angry_cat),
                contentDescription = "화난 고양이",
                Modifier.size(33.dp)
            )

            HorizontalDivider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(50.dp)
                    .align(Alignment.CenterVertically)
            )

            Text(
                text = date,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            HorizontalDivider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(50.dp)
                    .align(Alignment.CenterVertically)
            )

            Image(
                painter = painterResource(id = R.drawable.icon_angry_cat),
                contentDescription = "화난 고양이",
                Modifier.size(33.dp)
            )
        }

        diaries.forEach {
            ContentItemView(time = it.time, content = it.content)
        }

    }
}


@Composable
fun ContentItemView(time: String, content: String) {  // 날짜별 다이어리 내용

    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .border(1.dp, Gray, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = RedWhite,
            contentColor = Color.Black,

            )
    ) {
        Column {
            Text(text = "작성 시간 : $time", modifier = Modifier.padding(6.dp), color = Gray)
            Text(text = content, modifier = Modifier.padding(6.dp), fontSize = 18.sp)
        }
    }
}


