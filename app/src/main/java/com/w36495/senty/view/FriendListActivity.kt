package com.w36495.senty.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.R
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.ActivityFriendListBinding
import com.w36495.senty.view.adapter.FriendAdapter
import com.w36495.senty.view.listener.FriendSelectListener
import com.w36495.senty.viewModel.FriendViewModel

class FriendListActivity : AppCompatActivity(), FriendSelectListener {

    private lateinit var binding: ActivityFriendListBinding
    private lateinit var friendViewModel: FriendViewModel

    private lateinit var friendAdapter: FriendAdapter

    private lateinit var resultFriendInfo: ActivityResultLauncher<Intent>
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        friendAdapter = FriendAdapter(this)
        friendViewModel = ViewModelProvider(this)[FriendViewModel::class.java]
        progressDialog = ProgressDialog(this)

        MobileAds.initialize(this) {}
        binding.adView.loadAd(AdRequest.Builder().build())

        // 친구 정보 등록 버튼 클릭
        binding.fabFriendAdd.setOnClickListener {
            addFriend()
        }

        // 새로운 친구의 정보가 등록되었을 때
        resultFriendInfo =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.let {
                        val addFriendInfo = it.getSerializableExtra("saveFriend") as Friend
                        friendViewModel.addFriend(addFriendInfo)
                    }
                }
            }

        // 친구의 정보가 수정되었을 때
        if (intent.hasExtra("saveFriend")) {
            val updateFriendInfo = intent.getSerializableExtra("saveFriend") as Friend
            val oldFriendImagePath: String? = intent.getStringExtra("oldFriendImagePath")
            friendViewModel.updateFriend(updateFriendInfo, oldFriendImagePath)
        }
        // 친구 정보 삭제
        else if (intent.hasExtra("deleteFriend")) {
            val deleteFriend = intent.getSerializableExtra("deleteFriend") as Friend
            friendViewModel.removeFriend(deleteFriend)
        }

        // 로그아웃 / 회원탈퇴 메뉴 클릭시
        binding.friendListToolbar.setOnMenuItemClickListener { menu ->
            val user = FirebaseAuth.getInstance().currentUser!!
            val mainIntent = Intent(this, MainActivity::class.java)

            when (menu.itemId) {
                R.id.user_logout -> {
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {
                        Toast.makeText(this, getString(R.string.toast_user_logout), Toast.LENGTH_SHORT).show()
                        finishAffinity()
                        startActivity(mainIntent)
                    }
                    true
                }
                R.id.user_delete -> {
                    MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                        .setTitle(R.string.msg_user_delete_title)
                        .setMessage(R.string.msg_user_delete_content)
                        .setNeutralButton(resources.getText(R.string.btn_cancel)) { _, _ -> }
                        .setPositiveButton(resources.getString(R.string.btn_delete)) { _, _ ->
                            user.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        friendViewModel.removeUser()
                                        Toast.makeText(this, getString(R.string.toast_user_delete), Toast.LENGTH_SHORT).show()
                                        finishAffinity()
                                        startActivity(mainIntent)
                                    }
                                }
                            finish()
                        }
                        .show()
                    true
                }
                else -> false
            }
        }

        binding.friendRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.friendRecyclerView.setHasFixedSize(true)
        binding.friendRecyclerView.adapter = friendAdapter
    }

    /**
     * 친구 등록 액티비티 호출
     */
    private fun addFriend() {
        val intent = Intent(this, FriendAddActivity::class.java)
        resultFriendInfo.launch(intent)
    }

    /**
     * 친구 정보 조회
     */
    override fun onFriendInfoClicked(friend: Friend) {
        val showFriendIntent = Intent(this, FriendInfoActivity::class.java)
        showFriendIntent.putExtra("showFriendInfo", friend)
        startActivity(showFriendIntent)
    }

    override fun onResume() {
        super.onResume()
        friendViewModel.friendList.observe(this) { friend ->
            friendAdapter.setFriendList(friend)
        }
        friendViewModel.friendProgress.observe(this) { progress ->
            showProgressDialog(progress)
        }
        friendViewModel.friendListToast.observe(this) { exception ->
            Toast.makeText(this, exception, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Progress 다이얼로그 호출
     */
    private fun showProgressDialog(progress: Double) {
        if (progress == 0.0) {
            progressDialog.show()
        } else if (progress == 100.0) {
            progressDialog.dismiss()
        }
    }

}