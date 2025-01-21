package com.dev.angry_diary.di

import android.content.Context
import androidx.room.Room
import com.example.composediary.data.local.dao.DiaryDao
import com.example.composediary.data.local.database.AppDatabase
import com.example.composediary.data.repository.DiaryRepository
import com.example.composediary.data.repository.DiaryRepositoryImpl
import com.example.composediary.data.repository.LockRepository
import com.example.composediary.data.repository.LockRepositoryImpl
import com.example.composediary.data.repository.UserRepository
import com.example.composediary.data.repository.UserRepositoryImpl
import com.example.composediary.util.LockPreferenceUtil
import com.example.composediary.util.UserPreferenceUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideDiaryDao(appDatabase: AppDatabase): DiaryDao {
        return appDatabase.diaryDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository( userPref: UserPreferenceUtil): UserRepository {
        return UserRepositoryImpl( userPref)
    }

    @Provides
    @Singleton
    fun provideDiaryRepository(diaryDao: DiaryDao, userPref: UserPreferenceUtil): DiaryRepository {
        return DiaryRepositoryImpl(diaryDao, userPref)
    }

    @Provides
    @Singleton
    fun provideUserPreferenceUtil(@ApplicationContext context: Context): UserPreferenceUtil {
        return UserPreferenceUtil(context)
    }

    @Provides
    @Singleton
    fun provideLockPreferenceUtil(@ApplicationContext context: Context): LockPreferenceUtil {
        return LockPreferenceUtil(context)
    }

    @Provides
    @Singleton
    fun provideLockRepository(lockPreferenceUtil: LockPreferenceUtil): LockRepository {
        return LockRepositoryImpl(lockPreferenceUtil)
    }

}