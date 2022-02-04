package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.w36495.senty.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 로그인 액티비티로 이동
        binding.mainEmailLogin.setOnClickListener {
            val emailLoginIntent = Intent(this, SignInActivity::class.java)
            startActivity(emailLoginIntent)
        }

        // TODO 구글 이메일 로그인
    }
}