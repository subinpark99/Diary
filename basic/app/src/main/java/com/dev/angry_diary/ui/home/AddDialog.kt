package com.dev.angry_diary.ui.home


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.dev.angry_diary.data.local.model.Diary
import com.dev.angry_diary.databinding.DialogAddBinding
import com.dev.angry_diary.ui.viewmodel.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class AddDialog : DialogFragment() {

    private lateinit var binding: DialogAddBinding
    private val diaryViewModel: DiaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddBinding.inflate(inflater, container, false)

        initView()
        clickListener()

        return binding.root
    }

    private fun initView() {

        isCancelable = true   // 뒤로가기 버튼, 빈 화면 터치를 통해 dialog 사라짐
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // background 투명하게
    }


    private fun clickListener() {
        // 저장하기
        binding.saveTv.setOnClickListener {
            val contents = binding.addDiaryTv.text.toString()

            // 입력하지 않았을 때
            if (TextUtils.isEmpty(contents)) { Toast.makeText(context, " 내용이 없어요!", Toast.LENGTH_SHORT).show() }

            // 입력 창이 비어 있지 않을 때
            else {
                // 현재 날짜와 시간 가져오기
                val now = LocalDateTime.now()

                // 날짜와 시간 포맷 정의
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                // 날짜와 시간을 문자열로 변형
                val date = now.format(dateFormatter) // 예: "2024-07-23"
                val time = now.format(timeFormatter)

                val diary = Diary(contentId = 0, content = contents, date = date, time = time)
                diaryViewModel.addDiary(diary)

                dismiss()
            }
        }

        // 취소하기
        binding.backTv.setOnClickListener {
            dismiss()
        }
    }


}