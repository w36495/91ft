package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            val userEmail = binding.signinEmail.text.toString()
            val userPassword = binding.signinPasswd.text.toString()
            signIn(userEmail, userPassword)
        }

        // 회원가입 버튼 클릭
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 비밀번호 찾기 버튼 클릭
        binding.signinForgetPasswd.setOnClickListener {
            ResetPasswdDialog().show(supportFragmentManager, "resetPasswdDialog")
        }

    }

    // firebase 로그인
    private fun signIn(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, FriendListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "이메일 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }



    // 입력값(이메일, 비밀번호) 유효값 검사
    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.signinEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.signinEmail.error = "필수입력입니다."
            valid = false
        } else {
            binding.signinEmail.error = null
        }

        val password = binding.signinPasswd.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.signinPasswd.error = "필수입력입니다."
            valid = false
        } else {
            binding.signinPasswd.error = null
        }

        return valid
    }

}