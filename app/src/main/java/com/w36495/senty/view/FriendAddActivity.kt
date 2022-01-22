package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.w36495.senty.databinding.ActivityFriendAddBinding

class FriendAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendAddBinding

    private var isUpdate : Boolean = false  // 친구 정보 등록 : false, 친구 정보 수정 : true
    private lateinit var friendKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.friendAddActivity = this@FriendAddActivity

        if (intent.hasExtra("friendName")) {
            binding.friendAddName.setText(intent.getStringExtra("friendName"))
            binding.friendAddPhone.setText(intent.getStringExtra("friendPhone"))
            friendKey = intent.getStringExtra("friendKey").toString()
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

        // 친구의 정보 수정
        if (isUpdate) {
            intent.putExtra("friendKey", friendKey)
            startActivity(intent)
        }
        // 친구의 정보 등록
        else {
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}