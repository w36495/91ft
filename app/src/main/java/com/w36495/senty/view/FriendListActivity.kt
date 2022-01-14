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

        friendListViewModel = ViewModelProvider(this)[FriendListViewModel::class.java]
        friendAdapter = FriendAdapter(this, this)

        binding.fabFriendAdd.setOnClickListener {
            openFriendAddActivity()
        }

        friendListViewModel.addFriendInfo(Friend("김철수", "010-9999-4444", "img"))
        friendListViewModel.addFriendInfo(Friend("김방구", "010-4444-2222", "img"))

        // 새로운 친구의 정보가 등록되었을 때
        resultFriendInfo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val friendName = result.data?.getStringExtra("friendName") ?: ""
                val friendPhone = result.data?.getStringExtra("friendPhone") ?: ""
                friendListViewModel.addFriendInfo(Friend(friendName, friendPhone, "img"))
            }
        }

        // 친구의 정보가 수정되었을 때
        if (intent.hasExtra("friendName")) {
            friendListViewModel.updateFriendInfo(
                Friend(
                    intent.getStringExtra("friendName")!!,
                    intent.getStringExtra("friendPhone")!!,
                    "img"),
                intent.getIntExtra("friendPosition", -1))
        }

        binding.friendRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.friendRecyclerView.setHasFixedSize(true)
        binding.friendRecyclerView.adapter = friendAdapter


    }

    /**
     * 친구 등록 액티비티 호출
     */
    private fun openFriendAddActivity() {
        val intent = Intent(this, FriendAddActivity::class.java)
        resultFriendInfo.launch(intent)
    }

    /**
     * 친구 정보 조회 다이얼로그 호출
     */
    override fun onFriendInfoClicked(friend: Friend, position: Int) {
        FriendDetailDialog(friend, position).show(supportFragmentManager, "friendInfoDialog")
    }

    override fun onResume() {
        super.onResume()
        friendListViewModel.friendList.observe(this, { friend ->
            friendAdapter.setFriendList(friend!!)
        })
    }

}