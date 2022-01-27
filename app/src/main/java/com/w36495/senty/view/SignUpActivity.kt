package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val userEmail = binding.signupEmail.text.toString()
            val userPassword = binding.signupPasswd.text.toString()
            createAccount(userEmail, userPassword)
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
                    Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 입력값(이메일, 비밀번호) 유효값 검사
    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.signupEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.signupEmail.error = "필수입력입니다."
            valid = false
        } else {
            binding.signupEmail.error = null
        }

        val password = binding.signupPasswd.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.signupPasswd.error = "필수입력입니다."
            valid = false
        } else {
            binding.signupPasswd.error = null
        }

        return valid
    }
}