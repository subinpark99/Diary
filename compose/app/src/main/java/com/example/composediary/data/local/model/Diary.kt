package com.example.composediary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class Diary(
    @PrimaryKey(autoGenerate = true)
    var contentId: Int = 0,
    var content: String,
    val date: String,
    val time: String,
    var userId: Long = 0L
)

@Entity
data class DiaryMonthlyCount(
    @PrimaryKey val month: String,
    val count: Int,
)
