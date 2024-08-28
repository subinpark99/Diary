package com.dev.angry_diary.di

import android.content.Context
import androidx.room.Room
import com.dev.angry_diary.data.local.dao.DiaryDao
import com.dev.angry_diary.data.local.dao.UserDao
import com.dev.angry_diary.data.local.database.AppDatabase
import com.dev.angry_diary.data.repository.DiaryRepository
import com.dev.angry_diary.data.repository.DiaryRepositoryImpl
import com.dev.angry_diary.data.repository.LockRepository
import com.dev.angry_diary.data.repository.LockRepositoryImpl
import com.dev.angry_diary.data.repository.UserRepository
import com.dev.angry_diary.data.repository.UserRepositoryImpl
import com.dev.angry_diary.util.LockPreferenceUtil
import com.dev.angry_diary.util.UserPreferenceUtil
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
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideDiaryDao(appDatabase: AppDatabase): DiaryDao {
        return appDatabase.diaryDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, userPref: UserPreferenceUtil): UserRepository {
        return UserRepositoryImpl(userDao, userPref)
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