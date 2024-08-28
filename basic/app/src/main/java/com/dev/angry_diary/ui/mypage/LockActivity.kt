package com.dev.angry_diary.ui.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dev.angry_diary.R
import com.dev.angry_diary.util.AppLockConst
import com.dev.angry_diary.ui.viewmodel.LockViewModel
import com.dev.angry_diary.databinding.ActivityPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordBinding

    private val viewModel: LockViewModel by viewModels()

    private lateinit var etPasscodes: List<EditText>
    private lateinit var etInputInfo: TextView

    private var oldPwd = ""
    private var changePwdUnlock = false
    private var type by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButton()
    }

    // 버튼과 입력 텍스트 리스트 설정
    private fun setButton() {
        etPasscodes = listOf(
            binding.etPasscode1,
            binding.etPasscode2,
            binding.etPasscode3,
            binding.etPasscode4
        )

        etInputInfo = binding.etInputInfo

        // 각 EditText에 TextWatcher 추가
        etPasscodes.forEach { et ->
            et.addTextChangedListener(textWatcher)
        }

        val buttonArray = arrayOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9,
            binding.btnclear, binding.btndelete
        )

        buttonArray.forEach { it.setOnClickListener(btnListener) } // 각 버튼에 클릭리스너 적용
        etPasscodes.first().requestFocus() // 최초 실행 시 첫 입력칸에 포커스

        type = intent.getIntExtra("type", 0)
    }


    // TextWatcher를 통한 입력 감시
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            // 모든 입력칸이 채워졌는지 확인
            if (etPasscodes.all { it.text.isNotEmpty() }) {
                inputType(type) // 모든 입력이 완료되면 inputType 호출
            }
        }
    }

    // 버튼 클릭 했을 떄
    private val btnListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn0 -> inputButton(0)
            R.id.btn1 -> inputButton(1)
            R.id.btn2 -> inputButton(2)
            R.id.btn3 -> inputButton(3)
            R.id.btn4 -> inputButton(4)
            R.id.btn5 -> inputButton(5)
            R.id.btn6 -> inputButton(6)
            R.id.btn7 -> inputButton(7)
            R.id.btn8 -> inputButton(8)
            R.id.btn9 -> inputButton(9)
            R.id.btnclear -> onClear()
            R.id.btndelete -> onDeleteKey()
        }
    }


    // 숫자 버튼 입력
    private fun inputButton(number: Int) {
        for (et in etPasscodes) {
            if (et.isFocused) {
                et.setText(number.toString())
                setEditText(et)
                break
            }
        }
    }


    // 한 칸씩 지우기
    @SuppressLint("SetTextI18n")
    private fun onDeleteKey() {

        val focusedIndex = etPasscodes.indexOfFirst { it.isFocused }
        if (focusedIndex >= 0) {
            etPasscodes[focusedIndex].setText("")
            if (focusedIndex > 0) {
                etPasscodes[focusedIndex - 1].requestFocus()
            }
        }
    }


    // 모두 지우기
    @SuppressLint("SetTextI18n")
    private fun onClear() {
        etPasscodes.forEach { it.setText("") }
        etPasscodes.first().requestFocus()
    }


    // 입력된 비밀번호를 합치기
    private fun inputedPassword(): String {
        return etPasscodes.joinToString("") { it.text.toString() }
    }


    // EditText 설정
    @SuppressLint("SetTextI18n")
    private fun setEditText(
        currentEditText: EditText,
    ) {
        val currentIndex = etPasscodes.indexOf(currentEditText)
        if (currentIndex < etPasscodes.size - 1) {
            etPasscodes[currentIndex + 1].requestFocus()
            etPasscodes[currentIndex + 1].setText("")
        }
    }

    // intent로 받은 type 별 로 동작 실행
    private fun inputType(type: Int) {
        when (type) {
            AppLockConst.ENABLE_PASSLOCK -> enableLock()
            AppLockConst.DISABLE_PASSLOCK -> disableLock()
            AppLockConst.UNLOCK_PASSWORD -> unlockPassword()
            AppLockConst.CHANGE_PASSWORD -> changePassword()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun enableLock() {  // 잠금 활성화

        if (oldPwd.isEmpty()) {
            oldPwd = inputedPassword()
            onClear()
            etInputInfo.text = "다시 한번 입력하세요"
        } else {
            if (oldPwd == inputedPassword()) {
                viewModel.setPassword(inputedPassword())
                setResult(Activity.RESULT_OK)
                showToast("잠금 설정됨")
               finish()
            } else {
                onClear()
                oldPwd = ""
                etInputInfo.text = "비밀번호가 틀립니다"
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun disableLock() = // 잠금 비활성화

        if (viewModel.unlockPassword(inputedPassword())) {
            viewModel.removePassword()
            showToast("잠금 해제됨")
            finish()
        } else {
            onClear()
            etInputInfo.text = "비밀번호가 틀립니다"
        }


    @SuppressLint("SetTextI18n")
    private fun unlockPassword() {  // 잠금 풀기

        if (viewModel.unlockPassword(inputedPassword())) {
            setResult(Activity.RESULT_OK)  // 잠금 해제 완료
            finish()
        } else {
            // 비밀번호가 일치하지 않으면 에러 표시
            onClear()
            etInputInfo.text = "비밀번호가 틀립니다"
        }
    }


    @SuppressLint("SetTextI18n")
    private fun changePassword() {  // 비밀번호 변경

        val inputPassword = inputedPassword()

        if (!changePwdUnlock) {
            // 기존 비밀번호 확인 단계
            if (viewModel.unlockPassword(inputPassword)) {
                // 기존 비밀번호가 일치하면 새로운 비밀번호를 입력하도록 설정
                changePwdUnlock = true
                oldPwd = ""
                onClear()
                etInputInfo.text = "새로운 비밀번호 입력"
            } else {
                // 기존 비밀번호가 일치하지 않는 경우
                etInputInfo.text = "비밀번호가 틀립니다"
                onClear()
            }
        } else {
            // 새로운 비밀번호 설정 단계
            if (oldPwd.isEmpty()) {
                // 첫 번째 새로운 비밀번호 입력
                oldPwd = inputPassword
                onClear()
                etInputInfo.text = "새로운 비밀번호 다시 입력"
            } else {
                // 두 번째 새로운 비밀번호 입력
                if (oldPwd == inputPassword) {
                    viewModel.setPassword(inputPassword)
                    changePwdUnlock = false

                    showToast("비밀번호 변경됨")
                    finish()

                } else {
                    // 새로운 비밀번호가 일치하지 않는 경우
                    onClear()
                    oldPwd = ""
                    etInputInfo.text = "새로운 비밀번호가 일치하지 않습니다"
                }
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
