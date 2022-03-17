package com.w36495.senty.view

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.R
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.ActivityFriendInfoBinding
import com.w36495.senty.viewModel.FriendViewModel

class FriendInfoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityFriendInfoBinding
    private lateinit var friend: Friend

    private val friendViewModel by viewModels<FriendViewModel>()

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

        binding.friendInfoGift.setOnClickListener(this)
        binding.friendInfoUpdate.setOnClickListener(this)
        binding.friendInfoDelete.setOnClickListener(this)
        binding.friendInfoToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.friend_info_gift -> {
                val giftListIntent = Intent(this, GiftListActivity::class.java)
                startActivity(giftListIntent)
            }
            R.id.friend_info_update -> {
                val updateFriendInfoIntent = Intent(this, FriendAddActivity::class.java)
                updateFriendInfoIntent.putExtra("updateFriend", friend)
                startActivity(updateFriendInfoIntent)
            }
            R.id.friend_info_delete -> {
                showFriendInfoDeleteDialog()
            }
        }
    }

    private fun showFriendInfoDeleteDialog() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(R.string.msg_friend_delete_title)
            .setMessage(R.string.msg_friend_delete_content)
            .setNeutralButton(resources.getText(R.string.btn_cancel)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.btn_delete)) { _, _ ->
                friendViewModel.removeFriend(friend)
                val moveFriendListIntent = Intent(this, FriendListActivity::class.java)
                moveFriendListIntent.flags = FLAG_ACTIVITY_CLEAR_TOP
                startActivity(moveFriendListIntent)
                Toast.makeText(this, R.string.msg_friend_delete, Toast.LENGTH_SHORT).show()
                finish()
            }
            .show()
    }
}