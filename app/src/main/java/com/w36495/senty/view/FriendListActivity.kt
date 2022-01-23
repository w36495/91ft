package com.w36495.senty.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.ActivityFriendListBinding
import com.w36495.senty.view.adapter.FriendAdapter
import com.w36495.senty.view.listener.FriendSelectListener
import com.w36495.senty.viewModel.FriendListViewModel

class FriendListActivity : AppCompatActivity(), FriendSelectListener {

    private lateinit var binding: ActivityFriendListBinding
    private lateinit var friendListViewModel: FriendListViewModel

    private lateinit var friendAdapter: FriendAdapter

    private lateinit var resultFriendInfo: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        friendAdapter = FriendAdapter(this, this)
        friendListViewModel = ViewModelProvider(this)[FriendListViewModel::class.java]

        // 친구 정보 등록 버튼 클릭
        binding.fabFriendAdd.setOnClickListener {
            addFriend()
        }

        // 새로운 친구의 정보가 등록되었을 때
        resultFriendInfo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val friendKey = result.data?.getStringExtra("friendKey") ?: ""
                val friendName = result.data?.getStringExtra("friendName") ?: ""
                val friendPhone = result.data?.getStringExtra("friendPhone") ?: ""

                friendListViewModel.addFriendInfo(Friend(friendKey, friendName, friendPhone))
            }
        }

        // 친구의 정보가 수정되었을 때
        if (intent.hasExtra("friendName")) {
            friendListViewModel.updateFriendInfo(
                Friend(
                    intent.getStringExtra("friendKey")!!,
                    intent.getStringExtra("friendName")!!,
                    intent.getStringExtra("friendPhone")!!))
        }
        // 친구 정보 삭제
        else if (intent.hasExtra("deleteFriendKey")) {
            friendListViewModel.removeFriend(intent.getStringExtra("deleteFriendKey")!!)
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
        friendListViewModel.friendList.observe(this, { friend ->
            friendAdapter.setFriendList(friend)
        })
    }
}