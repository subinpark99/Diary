package com.example.composediary.data.repository

import com.example.composediary.data.local.dao.DiaryDao
import com.example.composediary.data.local.model.Diary
import com.example.composediary.data.local.model.DiaryMonthlyCount
import kotlinx.coroutines.flow.Flow
import com.example.composediary.util.UserPreferenceUtil
import javax.inject.Inject


interface DiaryRepository {
    suspend fun insertDiary(diary: Diary)
    suspend fun deleteAllDiary()
    fun getDiariesByMonth(month: String): Flow<List<Diary>>
    fun getDiaryCount(): Flow<Int>
    fun getDiaryMonthlyCount(year: String): Flow<List<DiaryMonthlyCount>>
}

class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao,
    private val userPreferences: UserPreferenceUtil,
) : DiaryRepository {


    // 토큰 조회
    private fun getUserTokenId(): String {
        return userPreferences.getTokenId() ?: "NoToken" // 기본값 설정
    }

    override suspend fun insertDiary(diary: Diary) {
        val tokenId = getUserTokenId()
        val diaryWithToken = diary.copy(tokenId = tokenId) // 토큰을 추가한 다이어리 객체 생성
        diaryDao.addContent(diaryWithToken)
    }

    override suspend fun deleteAllDiary() {
        diaryDao.deleteAll(getUserTokenId())
    }

    override fun getDiariesByMonth(month: String): Flow<List<Diary>> =
        diaryDao.readMonthData(month, getUserTokenId())

    override fun getDiaryCount(): Flow<Int> = diaryDao.getContentsCount(getUserTokenId())

    override fun getDiaryMonthlyCount(year: String): Flow<List<DiaryMonthlyCount>> =
        diaryDao.getDiaryMonthlyCount(year, getUserTokenId())


}