package com.dev.angry_diary.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composediary.data.local.model.Diary
import com.example.composediary.data.local.model.DiaryMonthlyCount
import com.example.composediary.data.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
) : ViewModel() {


    // 현재 월을 관리하는 StateFlow
    private val _currentMonthState: MutableStateFlow<YearMonth> =
        MutableStateFlow(getCurrentMonth())
    val currentMonthState: StateFlow<YearMonth> = _currentMonthState

    // 특정 월의 다이어리 리스트를 StateFlow로 관리
    @OptIn(ExperimentalCoroutinesApi::class)
    val diariesState: StateFlow<List<Diary>> = currentMonthState
        .flatMapLatest { month ->
            diaryRepository.getDiariesByMonth(month.format(DateTimeFormatter.ofPattern("yyyy-MM")))
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val monthlyDiaryCountState: StateFlow<List<DiaryMonthlyCount>> = diaryRepository
        .getDiaryMonthlyCount(getCurrentYear()).map { list ->
            val months =
                listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
            val monthMap = list.associate { it.month to it.count }
            months.map { DiaryMonthlyCount(it, monthMap[it] ?: 0) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 다이어리 개수를 StateFlow로 관리
    val diariesCountState: StateFlow<Int> = diaryRepository
        .getDiaryCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // 다이어리 추가
    fun addDiary(diary: Diary) {
        viewModelScope.launch {
            diaryRepository.insertDiary(diary)
        }
    }

    // 모든 다이어리 삭제
    fun deleteAllDiaries() {
        viewModelScope.launch {
            diaryRepository.deleteAllDiary()
        }
    }


    // 이전 달로 이동
    fun goToPreviousMonth() {
        _currentMonthState.value = _currentMonthState.value.minusMonths(1)
    }

    // 다음 달로 이동
    fun goToNextMonth() {
        _currentMonthState.value = _currentMonthState.value.plusMonths(1)
    }

    // 현재 월을 가져오는 함수
    private fun getCurrentMonth(): YearMonth {
        return YearMonth.now()
    }

    // 현재 연도를 가져오는 함수
    private fun getCurrentYear(): String {
        return LocalDate.now().year.toString()
    }
}
