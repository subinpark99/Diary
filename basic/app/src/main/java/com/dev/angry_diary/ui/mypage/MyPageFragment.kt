package com.dev.angry_diary.ui.mypage


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dev.angry_diary.R
import com.dev.angry_diary.util.AppLockConst
import com.dev.angry_diary.databinding.FragmentMypageBinding
import com.dev.angry_diary.ui.login.LoginActivity
import com.dev.angry_diary.ui.viewmodel.DiaryViewModel
import com.dev.angry_diary.ui.viewmodel.LockViewModel
import com.dev.angry_diary.ui.viewmodel.UserViewModel
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MyPageFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    interface DrawerInterface {
        fun showNavAndFab()
        fun hideNavAndFab()
    }

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val diaryViewModel: DiaryViewModel by viewModels()
    private val lockViewModel: LockViewModel by viewModels()

    private lateinit var drawerLayout: DrawerLayout
    private var drawerControl: DrawerInterface? = null
    private lateinit var password: String
    private val diaryCounts = mutableMapOf<String, Int>() // 월별 다이어리 개수를 저장할 변수

    override fun onAttach(context: Context) { // fragment에 activity 부착
        super.onAttach(context)
        if (context is DrawerInterface) {
            drawerControl = context
        } else {
            throw RuntimeException("$context")
        }
    }

    override fun onDetach() {  // fragment와 activity 분리
        super.onDetach()
        drawerControl = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMypageBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        setDrawer()
        setChart()
    }


    @SuppressLint("SetTextI18n")
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    userViewModel.userState.collect { result ->
                        binding.usernameTv.text = result.userName
                    }
                }

                launch {
                    diaryViewModel.diariesCountState.collect { result ->
                        binding.usercountTv.text = "$result 개 "
                    }
                }


                launch {
                    diaryViewModel.monthlyDiaryCountState.collectLatest { result ->
                        result.forEach { (month, count) ->
                            diaryCounts[month] = count
                        }
                        setupBarChart(generateDataEntries())

                    }
                }

                lifecycleScope.launch {
                    lockViewModel.password.collect { pwd ->
                        password = pwd ?: ""
                    }
                }
            }
        }
    }

    // drawerLayout 연결
    private fun setDrawer() {

        drawerLayout = binding.drawerLayout
        binding.navView.setNavigationItemSelectedListener(this)
        binding.drawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                drawerControl?.showNavAndFab()
            }

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    drawerControl?.hideNavAndFab()
                }
            }
        })
    }


    // 토스트 띄우고 화면 종료
    private fun lockedState(message: String) {
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // 메뉴 클릭 아이템 동작
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setLock -> {
                if (password.isNotEmpty()) lockedState("비밀번호가 이미 존재함")
                else navigateToLock(AppLockConst.ENABLE_PASSLOCK)
            }

            R.id.offLock -> {
                if (password.isEmpty()) lockedState("비밀번호가 존재하지 않음")
                else navigateToLock(AppLockConst.DISABLE_PASSLOCK)
            }

            R.id.changePw -> {
                if (password.isEmpty()) lockedState("비밀번호가 존재하지 않음")
                else navigateToLock(AppLockConst.CHANGE_PASSWORD)
            }

            R.id.logout -> logout()
            R.id.withdraw -> withdraw()
        }
        return false
    }

    // 로그아웃
    private fun logout() {
        userViewModel.logout()
        lockViewModel.removePassword()

        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        requireActivity().finish()
    }

    // 회원 탈퇴
    private fun withdraw() {
        userViewModel.withDraw()
        diaryViewModel.deleteAllDiaries()
        lockViewModel.removePassword()

        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        requireActivity().finish()
    }


    // 차트 세팅
    private fun setChart() {
        binding.chart.apply {
            description.isEnabled = false
            setMaxVisibleValueCount(12)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)
            setDrawBorders(false)
            legend.isEnabled = true
            setTouchEnabled(false)
            isDoubleTapToZoomEnabled = false
            animateY(2000)
        }
        setXAxis()
        setYAxis()
    }

    // 바 차트 데이터 설정
    private fun setupBarChart(dataEntries: List<BarEntry>) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val dataSet = BarDataSet(dataEntries, "${currentYear}년").apply {
            color = Color.parseColor("#EE6C4D")
        }
        binding.chart.data = BarData(dataSet)
    }


    private fun generateDataEntries(): List<BarEntry> {
        // diaryCounts의 키를 월로 사용하고, 값을 다이어리 개수로 사용
        return diaryCounts.entries
            .sortedBy { it.key } // 월 기준으로 정렬
            .mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value.toFloat())
            }
    }


    // X축 설정
    private fun setXAxis() {
        binding.chart.xAxis.apply {
            setDrawGridLines(false)
            isEnabled = true
            setDrawLabels(true)
            labelCount = 12
            valueFormatter = IndexAxisValueFormatter(getMonthLabels())
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(true)
        }
    }

    // Y축 설정
    private fun setYAxis() {
        binding.chart.axisLeft.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            isEnabled = true
            setDrawLabels(true)
        }
        binding.chart.axisRight.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }
    }

    // 월 라벨 생성
    private fun getMonthLabels(): List<String> {
        return listOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
    }

    private fun navigateToLock(type: Int) {

        val intent = Intent(activity, LockActivity::class.java).apply {
            putExtra("type", type)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        drawerControl?.showNavAndFab()
    }

}