package com.dev.angry_diary.ui.home


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dev.angry_diary.databinding.FragmentHomeBinding
import com.dev.angry_diary.ui.viewmodel.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val diaryViewModel: DiaryViewModel by viewModels()
    private lateinit var adapter: ListRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ListRVAdapter()
        binding.diaryrv.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeListState()
        clickListener()
    }

    private fun observeListState() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    diaryViewModel.diariesState.collectLatest { result ->

                        if (result.isEmpty()) {
                            binding.textview.visibility = View.VISIBLE
                            binding.diaryrv.visibility = View.GONE
                        } else {
                            adapter.submitDiaryList(result)
                            binding.textview.visibility = View.GONE
                            binding.diaryrv.visibility = View.VISIBLE
                        }
                    }
                }

                launch {
                    diaryViewModel.currentMonthState.collect { currentDate ->
                        val formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM")
                        val formattedMonth = currentDate.format(formatterMonth)
                        binding.monthTv.text = formattedMonth
                    }
                }
            }
        }
    }


    private fun clickListener() {

        binding.leftArrow.setOnClickListener {  // 이전 달
            diaryViewModel.goToPreviousMonth()
        }

        binding.rightArrow.setOnClickListener { // 다음 달
            diaryViewModel.goToNextMonth()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
