package com.dev.angry_diary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dev.angry_diary.data.local.model.Diary
import com.dev.angry_diary.data.local.model.DiaryMonthlyCount
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Insert
    suspend fun addContent(diary: Diary)

    @Query("DELETE FROM diary WHERE tokenId = :tokenId")
    suspend fun deleteAll(tokenId: String)

    // 해당 연월의 리스트 가져오기
    @Query("SELECT * FROM diary WHERE date LIKE :month  || '%' AND tokenId = :tokenId ORDER BY date DESC, time DESC")
    fun readMonthData(month: String, tokenId: String): Flow<List<Diary>>

    // 모든 다이어리 수
    @Query("SELECT COUNT(content) FROM diary WHERE tokenId=:tokenId")
    fun getContentsCount(tokenId: String): Flow<Int>

    /**
     * 특정 연도와 토큰 ID에 해당하는 월별 다이어리 개수를 가져옴
     */
    @Query("""
    SELECT strftime('%m', date) AS month, COUNT(*) AS count
    FROM diary 
    WHERE date LIKE :year || '%' AND tokenId = :tokenId 
    GROUP BY strftime('%m', date)
    ORDER BY month
""")
    fun getDiaryMonthlyCount(year: String, tokenId: String): Flow<List<DiaryMonthlyCount>>
}

