package com.example.composediary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.composediary.data.local.model.Diary
import com.example.composediary.data.local.model.DiaryMonthlyCount
import kotlinx.coroutines.flow.Flow

//@Dao
//interface DiaryDao {
//
//    // 다이어리 추가
//    @Insert
//    suspend fun addContent(diary: Diary)
//
//    // 토큰 ID에 해당하는 모든 다이어리 삭제
//    @Query("DELETE FROM diary WHERE tokenId = :tokenId")
//    suspend fun deleteAll(tokenId: String)
//
//    // 해당 연월의 리스트 가져오기
//    @Query("SELECT * FROM diary WHERE dateTime BETWEEN :startDate AND :endDate AND tokenId = :tokenId ORDER BY dateTime DESC")
//    fun readMonthData(startDate: LocalDateTime, endDate: LocalDateTime, tokenId: String): Flow<List<Diary>>
//
//    // 모든 다이어리 개수 가져오기
//    @Query("SELECT COUNT(content) FROM diary WHERE tokenId = :tokenId")
//    fun getContentsCount(tokenId: String): Flow<Int>
//
//    // 특정 연도에 해당하는 월별 다이어리 개수 가져오기
//    @Query("SELECT COUNT(*) FROM diary WHERE strftime('%Y', dateTime) = :year AND strftime('%m', dateTime) = :month AND tokenId = :tokenId")
//    fun getMonthlyDiaryCount(year: String, month: String, tokenId: String): Flow<Int>
//}


@Dao
interface DiaryDao {

    @Insert
    suspend fun addContent(diary: Diary)

    @Query("DELETE FROM diary WHERE userId = :userId")
    suspend fun deleteAll(userId: Long)

    // 해당 연월의 리스트 가져오기
    @Query("SELECT * FROM diary WHERE date LIKE :month  || '%' AND userId = :userId ORDER BY date DESC")
    fun readMonthData(month: String, userId: Long): Flow<List<Diary>>

    // 모든 다이어리 수
    @Query("SELECT COUNT(content) FROM diary WHERE userId = :userId")
    fun getContentsCount(userId: Long): Flow<Int>

    /**
     * 특정 연도와 user ID에 해당하는 월별 다이어리 개수를 가져옴
     */
    @Query("""
    SELECT strftime('%m', date) AS month, COUNT(*) AS count
    FROM diary
    WHERE date LIKE :year || '%' AND userId = :userId
    GROUP BY strftime('%m', date)
    ORDER BY month
""")
    fun getDiaryMonthlyCount(year: String, userId:Long): Flow<List<DiaryMonthlyCount>>
}

