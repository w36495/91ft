package com.w36495.senty.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.w36495.senty.R
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.ActivityFriendListBinding
import com.w36495.senty.view.adapter.FriendAdapter
import com.w36495.senty.viewModel.FriendListViewModel

class FriendListActivity : AppCompatActivity() {

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
        friendAdapter = FriendAdapter(this)

        binding.fabFriendAdd.setOnClickListener {
            openFriendAddActivity()
        }

        friendListViewModel.addFriendInfo(Friend("김철수", "010-9999-4444", "img"))
        friendListViewModel.addFriendInfo(Friend("김방구", "010-4444-2222", "img"))


        resultFriendInfo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val friendName = result.data?.getStringExtra("friendName") ?: ""
                val friendPhone = result.data?.getStringExtra("friendPhone") ?: ""
                friendListViewModel.addFriendInfo(Friend(friendName, friendPhone, "img"))
            }
        }
        binding.friendRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.friendRecyclerView.setHasFixedSize(true)
        binding.friendRecyclerView.adapter = friendAdapter

    }

    private fun openFriendAddActivity() {
//        val intent = Intent(this, FriendAddActivity::class.java)
//        resultFriendInfo.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        friendListViewModel.friendList.observe(this, androidx.lifecycle.Observer { friend ->
            friendAdapter.setFriendList(friend!!)
        })
    }
}