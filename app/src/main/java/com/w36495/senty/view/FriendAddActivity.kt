package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.w36495.senty.databinding.ActivityFriendAddBinding

class FriendAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.friendAddActivity = this@FriendAddActivity

    }

    fun saveFriendInfo() {
        val intent = Intent(this, FriendListActivity::class.java)
        intent.putExtra("friendName", binding.friendAddName.text.toString())
        intent.putExtra("friendPhone", binding.friendAddPhone.text.toString())
        setResult(RESULT_OK, intent)
        finish()
    }
}