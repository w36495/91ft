package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.w36495.senty.R
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.ActivityFriendInfoBinding

class FriendInfoActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityFriendInfoBinding
    private lateinit var friend: Friend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setInit()
    }

    fun setInit() {

        if (intent.hasExtra("friendKey")) {
            friend = Friend(
                intent.getStringExtra("friendKey").toString(),
                intent.getStringExtra("friendName").toString(),
                intent.getStringExtra("friendPhone").toString()
            )
        }

        binding.friendDetailName.text = friend.name
        binding.friendDetailPhone.text = friend.phone

        // 선물 목록으로 이동
        binding.friendInfoGift.setOnClickListener(this)
        // 친구 정보 수정으로 이동
        binding.friendInfoUpdate.setOnClickListener(this)
        // 친구 정보 삭제
        binding.friendInfoDelete.setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.friend_info_gift -> {
                val giftListIntent = Intent(this, GiftListActivity::class.java)
                giftListIntent.putExtra("friendKey", friend.key)
                giftListIntent.putExtra("friendName", friend.name)
                giftListIntent.putExtra("friendPhone", friend.phone)
                startActivity(giftListIntent)
            }
            R.id.friend_info_update -> {
                val updateIntent = Intent(this, FriendAddActivity::class.java)
                updateIntent.putExtra("friendKey", friend.key)
                updateIntent.putExtra("friendName", friend.name)
                updateIntent.putExtra("friendPhone", friend.phone)
                startActivity(updateIntent)
            }
            R.id.friend_info_delete -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.msg_friend_delete_title)
                    .setMessage(R.string.msg_friend_delete_content)
                    .setNeutralButton(resources.getText(R.string.btn_cancel)) {_, _ ->

                    }
                    .setPositiveButton(resources.getString(R.string.btn_delete)) {_, _ ->
                        val deleteIntnet = Intent(this, FriendListActivity::class.java)
                        deleteIntnet.putExtra("friendKey", friend.key)
                        setResult(RESULT_OK, deleteIntnet)
                        Toast.makeText(this, R.string.msg_friend_delete, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .show()

            }
        }
    }
}