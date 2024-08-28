package com.dev.angry_diary.ui.home


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dev.angry_diary.R
import com.dev.angry_diary.util.AppLockConst
import com.dev.angry_diary.ui.viewmodel.LockViewModel
import com.dev.angry_diary.databinding.ActivityMainBinding
import com.dev.angry_diary.ui.mypage.LockActivity
import com.dev.angry_diary.ui.mypage.MyPageFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    MyPageFragment.DrawerInterface {

    private lateinit var binding: ActivityMainBinding

    private lateinit var floating: FloatingActionButton
    private lateinit var appBar: BottomAppBar

    private lateinit var unlockLauncher: ActivityResultLauncher<Intent>
    private var isUnlockPerformed = false

    private val lockViewModel: LockViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        floating = binding.fabBtn
        appBar = binding.bottomAppBar

        checkLockState()
        setUnlock()
        showDialog()
        initNavigate()
    }


    // 잠금 해제 결과 받기
    private fun setUnlock() {

        unlockLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    isUnlockPerformed = true
                } else {
                    finish() // 잠금 해제가 되지 않았을 때 앱 종료
                }
            }
    }

    // 잠금 상태 확인
    private fun checkLockState() {

        lifecycleScope.launch {
            lockViewModel.password.collect { password ->
                if (password != null && !isUnlockPerformed) {
                    val intent = Intent(this@MainActivity, LockActivity::class.java).apply {
                        putExtra("type", AppLockConst.UNLOCK_PASSWORD)
                    }
                    unlockLauncher.launch(intent)
                }
            }
        }

    }

    // 플로팅 버튼 클릭리스너
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialog() {

        floating.setOnClickListener {
            AddDialog().show(supportFragmentManager, "s")
        }
    }

    private fun initNavigate() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navBar.setupWithNavController(navController)
        binding.navBar.background = null   // 바텀네비게이션 배경 없애기
    }


    override fun hideNavAndFab() {  // 네비게이션,플로팅 버튼 숨기기
        setFullScreen(0)

        floating.visibility = View.GONE
        appBar.visibility = View.GONE
    }

    override fun showNavAndFab() {  // 네비게이션,플로팅 버튼 보이기
        setFullScreen(30)

        floating.visibility = View.VISIBLE
        appBar.visibility = View.VISIBLE
    }

    private fun setFullScreen(margin: Int) {
        val layoutParams = binding.navHost.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        binding.navHost.layoutParams = layoutParams
    }

}
