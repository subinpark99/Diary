package com.dev.angry_diary.app

import android.app.Application
import com.example.composediary.BuildConfig
import com.jakewharton.threetenabp.AndroidThreeTen


import com.kakao.sdk.common.KakaoSdk

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // KaKao SDK  초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)

        // ThreeTenABP 초기화
        AndroidThreeTen.init(this)
    }
}
