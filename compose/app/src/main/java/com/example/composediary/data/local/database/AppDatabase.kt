package com.example.composediary.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composediary.data.local.dao.DiaryDao
import com.example.composediary.data.local.model.Diary


@Database(entities = [ Diary::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao
}