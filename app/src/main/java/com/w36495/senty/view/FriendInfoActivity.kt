package com.w36495.senty.view

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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

        MobileAds.initialize(this) {}
        binding.adView.loadAd(AdRequest.Builder().build())

        setInit(view)
    }

    private fun setInit(view: View) {
        friend = intent.getSerializableExtra("showFriendInfo") as Friend

        val sharedPref = getSharedPreferences("friend", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("friendKey", friend.key)
            putString("friendName", friend.name)
            putString("friendImagePath", friend.imagePath)
            commit()
        }

        binding.friendDetailName.text = friend.name
        friend.imagePath?.let { imgPath ->
            GlideApp.with(view)
                .load(Firebase.storage.reference.child(imgPath))
                .into(binding.friendInfoImg)
        }

        // 선물 목록으로 이동
        binding.friendInfoGift.setOnClickListener(this)
        // 친구 정보 수정으로 이동
        binding.friendInfoUpdate.setOnClickListener(this)
        // 친구 정보 삭제
        binding.friendInfoDelete.setOnClickListener(this)
        // 뒤로가기 버튼 클릭
        binding.friendInfoToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.friend_info_gift -> {
                // 선물 목록 버튼 클릭
                val giftListIntent = Intent(this, GiftListActivity::class.java)
                startActivity(giftListIntent)
            }
            R.id.friend_info_update -> {
                // 친구 정보 수정 버튼 클릭
                val updateIntent = Intent(this, FriendAddActivity::class.java)
                updateIntent.putExtra("updateFriend", friend)
                startActivity(updateIntent)
            }
            R.id.friend_info_delete -> {
                // 친구 정보 삭제 버튼 클릭
                MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle(R.string.msg_friend_delete_title)
                    .setMessage(R.string.msg_friend_delete_content)
                    .setNeutralButton(resources.getText(R.string.btn_cancel)) { _, _ -> }
                    .setPositiveButton(resources.getString(R.string.btn_delete)) { _, _ ->
                        val deleteIntnet = Intent(this, FriendListActivity::class.java)
                        deleteIntnet.putExtra("deleteFriend", friend)
                        deleteIntnet.flags = FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(deleteIntnet)
                        Toast.makeText(this, R.string.msg_friend_delete, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .show()
            }
        }
    }
}