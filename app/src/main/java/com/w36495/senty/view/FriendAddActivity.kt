package com.w36495.senty.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.R
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.ActivityFriendAddBinding
import com.w36495.senty.viewModel.FriendViewModel

class FriendAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendAddBinding
    private lateinit var resultGalleryImage: ActivityResultLauncher<Intent>
    private val friendViewModel by viewModels<FriendViewModel>()

    private lateinit var friendKey: String
    private var friendImageUri: Uri? = null
    private var oldFriendImagePath: String? = null
    private var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        MobileAds.initialize(this) {}
        binding.adView.loadAd(AdRequest.Builder().build())

        if (intent.hasExtra("updateFriend")) {
            isUpdate = true
            binding.friendAddToolbar.title = getString(R.string.toolbar_friend_update)

            val updateFriendInfo = intent.getSerializableExtra("updateFriend") as Friend
            friendKey = updateFriendInfo.key
            binding.friendAddName.setText(updateFriendInfo.name)
            oldFriendImagePath = updateFriendInfo.imagePath

            updateFriendInfo.imagePath?.let { imgPath ->
                GlideApp.with(view)
                    .load(Firebase.storage.reference.child(imgPath))
                    .into(binding.friendAddImg)
            }
        }

        binding.friendAddImgBtn.setOnClickListener {
            ImagePermission().getImageByGallery(view, resultGalleryImage)
        }

        binding.friendAddSave.setOnClickListener {
            saveFriend()
        }

        binding.friendAddToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        resultGalleryImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    setImageByGallery(result)
                }
            }
    }

    private fun setImageByGallery(result: ActivityResult) {
        friendImageUri = result.data?.data!!
        Glide.with(this)
            .load(friendImageUri)
            .into(binding.friendAddImg)
    }

    /**
     * 친구의 정보 저장
     */
    private fun saveFriend() {
        if (binding.friendAddName.text.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_empty_friend_title), Toast.LENGTH_SHORT)
                .show()
            return
        }

        val friend = Friend(
            if (isUpdate) friendKey else "",
            binding.friendAddName.text.toString(),
            if (isUpdate) {
                friendImageUri?.toString() ?: oldFriendImagePath
            } else {
                friendImageUri?.toString()
            }
        )

        // 친구의 정보 수정
        if (isUpdate) {
            friendViewModel.updateFriend(friend, oldFriendImagePath)
        } else {
            // 친구의 정보 등록
            friendViewModel.addFriend(friend)
        }

        val moveFriendListIntent = Intent(this, FriendListActivity::class.java)
        startActivity(moveFriendListIntent)
        finish()
    }
}