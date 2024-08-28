package com.dev.angry_diary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey var userEmail:  String = "",
    val userName: String = "",
    val tokenId: String = ""
)