package com.dev.angry_diary.data.local.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class Diary(
    @PrimaryKey(autoGenerate = true)
    var contentId: Int = 0,
    var content: String,
    var date: String,  // 2024-07-23
    var time: String,  // 12:23
    var tokenId: String="",
)

@Entity
data class DiaryMonthlyCount(
    @PrimaryKey val month: String,
    val count: Int,
)