package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.w36495.senty.databinding.ActivityFriendAddBinding

class FriendAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendAddBinding

    private var isUpdate : Boolean = false  // 친구 정보 등록 : false, 친구 정보 수정 : true
    private var friendPosition: Int = -1    // 수정되는 친구의 배열 위치

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.friendAddActivity = this@FriendAddActivity

        if (intent.hasExtra("friendName")) {
            binding.friendAddName.setText(intent.getStringExtra("friendName"))
            binding.friendAddPhone.setText(intent.getStringExtra("friendPhone"))
            friendPosition = intent.getIntExtra("friendPosition", -1)
            isUpdate = true
        }

    }

    /**
     * 친구의 정보 저장
     */
    fun saveFriendInfo() {
        val intent = Intent(this, FriendListActivity::class.java)
        intent.putExtra("friendName", binding.friendAddName.text.toString())
        intent.putExtra("friendPhone", binding.friendAddPhone.text.toString())

        // 친구의 정보를 수정하면 -> position도 함께 전달
        if (isUpdate) {
            intent.putExtra("friendPosition", friendPosition)
            startActivity(intent)
        }
        // 친구의 정보 등록
        else {
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}