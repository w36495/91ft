package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.R
import com.w36495.senty.databinding.ActivitySignupBinding
import com.w36495.senty.util.StringUtils

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        // 회원가입 버튼 클릭
        binding.btnSignup.setOnClickListener {
            val userEmail = binding.signupEmail.editText?.text.toString()
            val userPassword = binding.signupPasswd.editText?.text.toString()
            createAccount(userEmail, userPassword)
        }

        // 뒤로가기 버튼 클릭
        binding.signupToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // firebase 회원가입
    private fun createAccount(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.msg_complete_signup), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.msg_failed_signup), Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 입력값(이메일, 비밀번호) 유효값 검사
    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.signupEmail.editText?.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.signupEmail.error = getString(R.string.error_input)
            valid = false
        } else {
            // 이메일 형식 정규표현식 검사
            if (!StringUtils.isValidEmail(email)) {
                binding.signupEmail.error = getString(R.string.error_input_email)
                valid = false
            } else {
                binding.signupEmail.error = null
            }
        }

        val password = binding.signupPasswd.editText?.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.signupPasswd.error = getString(R.string.error_input)
            valid = false
        } else {
            // 길이가 8자미만일때
            if (password.length < 8) {
                binding.signupPasswd.error = getString(R.string.error_input_password_length)
                valid = false
            } else if (!StringUtils.isValidPassword(password)) {
                // 8자 이상인데 영문자/숫자로 이루어지지 않았을 때
                binding.signupPasswd.error = getString(R.string.error_input_password)
                valid = false
            } else {
                binding.signupPasswd.error = null
            }
        }

        val passwordCheck = binding.signupPasswdCheck.editText?.text.toString()
        if (TextUtils.isEmpty(passwordCheck)) {
            binding.signupPasswdCheck.error = getString(R.string.error_input)
            valid = false
        } else {
            // 비밀번호와 비밀번호 확인이 일치하지 않을 때
            if (password != passwordCheck) {
                binding.signupPasswdCheck.error = getString(R.string.error_input_password_check)
                valid = false
            } else {
                binding.signupPasswdCheck.error = null
            }
        }

        return valid
    }

}