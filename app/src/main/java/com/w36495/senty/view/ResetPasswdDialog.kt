package com.w36495.senty.view

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.R
import com.w36495.senty.databinding.DialogPasswdResetBinding
import com.w36495.senty.util.StringUtils

class ResetPasswdDialog : DialogFragment() {

    private lateinit var binding: DialogPasswdResetBinding
    private lateinit var size: Point

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogPasswdResetBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        // dialog 설정
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // device size
        val windowManager = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        size = Point()
        display.getSize(size)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 확인 버튼 클릭
        binding.resetPasswdCheck.setOnClickListener {
            val inputEmail = binding.resetEmail.text.toString()
            if (inputEmail.isEmpty()) {
                Toast.makeText(
                    view.context,
                    getString(R.string.toast_empty_email),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!StringUtils.isValidEmail(inputEmail)) {
                Toast.makeText(
                    view.context,
                    R.string.toast_email_invalid_exception,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sendPasswordResetEmail(view, inputEmail)
            }
        }

        // 닫기 버튼 클릭
        binding.resetPasswdToolbar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.close -> {
                    dismiss()
                    true
                }
                else -> false
            }
        }

    }

    // 비밀번호 재설정을 위해 이메일 전송
    private fun sendPasswordResetEmail(view: View, email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        view.context,
                        getString(R.string.toast_send_email),
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        // 디바이스 크기 -> 다이얼로그 크기 정하기
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}