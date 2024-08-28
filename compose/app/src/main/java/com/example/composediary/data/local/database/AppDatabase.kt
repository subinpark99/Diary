package com.example.composediary.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composediary.data.local.dao.DiaryDao
import com.example.composediary.data.local.dao.UserDao
import com.example.composediary.data.local.model.Diary
import com.example.composediary.data.local.model.User


@Database(entities = [User::class, Diary::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun diaryDao(): DiaryDao
}