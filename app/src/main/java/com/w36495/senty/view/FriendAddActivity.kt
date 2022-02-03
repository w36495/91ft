package com.w36495.senty.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.ActivityFriendAddBinding

class FriendAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendAddBinding
    private lateinit var resultGalleryImage: ActivityResultLauncher<Intent>

    private lateinit var friendKey: String
    private var friendImageUri: Uri? = null
    private var oldFriendImagePath: String? = null
    private var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra("updateFriend")) {
            isUpdate = true

            val updateFriendInfo = intent.getSerializableExtra("updateFriend") as Friend
            friendKey = updateFriendInfo.key
            binding.friendAddName.setText(updateFriendInfo.name)
            binding.friendAddPhone.setText(updateFriendInfo.phone)
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
        val friendAddIntent = Intent(this, FriendListActivity::class.java)

        val friend = Friend(
            if (isUpdate) friendKey else "",
            binding.friendAddName.text.toString(),
            binding.friendAddPhone.text.toString(),
            if (isUpdate) {
                friendImageUri?.toString() ?: oldFriendImagePath
            } else {
                friendImageUri?.toString()
            }

        )

        friendAddIntent.putExtra("saveFriend", friend)

        // 친구의 정보 수정
        if (isUpdate) {
            friendAddIntent.putExtra("oldFriendImagePath", oldFriendImagePath)
            startActivity(friendAddIntent)
            finish()
        }
        // 친구의 정보 등록
        else {
            setResult(RESULT_OK, friendAddIntent)
            finish()
        }
    }
}