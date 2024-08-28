package com.dev.angry_diary.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.angry_diary.data.local.dao.DiaryDao
import com.dev.angry_diary.data.local.dao.UserDao
import com.dev.angry_diary.data.local.model.Diary
import com.dev.angry_diary.data.local.model.User


@Database(entities = [User::class, Diary::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun diaryDao(): DiaryDao
}