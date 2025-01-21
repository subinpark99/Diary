package com.example.composediary.data.repository

import com.example.composediary.data.local.dao.DiaryDao
import com.example.composediary.data.local.model.Diary
import com.example.composediary.data.local.model.DiaryMonthlyCount
import com.example.composediary.util.UserPreferenceUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface DiaryRepository {
    suspend fun insertDiary(diary: Diary)
    suspend fun deleteAllDiary()
    fun getDiariesByMonth(month: String): Flow<List<Diary>>
    fun getDiaryCount(): Flow<Int>
    fun getDiaryMonthlyCount(year: String): Flow<List<DiaryMonthlyCount>>
    fun getUserId(): Long
}


class DiaryRepositoryImpl @Inject constructor(
    private val diaryDao: DiaryDao,
    private val userPreferences: UserPreferenceUtil,
) : DiaryRepository {

    override fun getUserId(): Long {
        return userPreferences.getUserId()
    }

    override suspend fun insertDiary(diary: Diary) {
        val userId = getUserId()
        val diaryWithId= diary.copy(userId = userId) // 사용자 아이디를 추가한 다이어리 객체 생성
        diaryDao.addContent(diaryWithId)
    }

    override suspend fun deleteAllDiary() {
        diaryDao.deleteAll(getUserId())
    }

    override fun getDiariesByMonth(month: String): Flow<List<Diary>> =
        diaryDao.readMonthData(month, getUserId())

    override fun getDiaryCount(): Flow<Int> = diaryDao.getContentsCount(getUserId())

    override fun getDiaryMonthlyCount(year: String): Flow<List<DiaryMonthlyCount>> =
        diaryDao.getDiaryMonthlyCount(year, getUserId())

}
