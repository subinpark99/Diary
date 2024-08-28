package com.example.composediary.navigation

import com.example.composediary.R

sealed class BottomNavItem(
    var title: String,
    var icon: Int, val route: String,
) {
    object Home :
        BottomNavItem(
            "Home",
            R.drawable.icon_home, HOME
        )

    object MyPage :
        BottomNavItem(
            "MyPage",
            R.drawable.icon_person, MYPAGE
        )

}

const val HOME = "HOME"
const val MYPAGE = "MYPAGE"
const val LOGIN = "LOGIN"
const val PASSWORD = "PASSWORD"

