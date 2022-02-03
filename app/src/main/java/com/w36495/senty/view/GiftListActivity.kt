package com.w36495.senty.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.ActivityGiftListBinding
import com.w36495.senty.view.adapter.GiftAdapter
import com.w36495.senty.view.listener.GiftSelectListener
import com.w36495.senty.viewModel.GiftViewModel

class GiftListActivity : AppCompatActivity(), GiftSelectListener {

    private lateinit var binding: ActivityGiftListBinding
    private lateinit var giftViewModel: GiftViewModel

    private lateinit var resultAddGift: ActivityResultLauncher<Intent>
    private lateinit var giftAdapter: GiftAdapter

    private lateinit var friend: Friend // 친구의 정보 저장 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGiftListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 친구의 정보를 클릭했을 때 저장했던 정보 불러오기
        val sharedPref = getSharedPreferences("friend", Context.MODE_PRIVATE)
        friend = Friend(
            sharedPref.getString("friendKey", "")!!,
            sharedPref.getString("friendName", "")!!,
            sharedPref.getString("friendPhone", "")!!,
            sharedPref.getString("friendImagePath", null)
        )

        giftAdapter = GiftAdapter(view.context, this)
        giftViewModel = ViewModelProvider(this, GiftViewModel.GiftViewModelFactory(friend.key))[GiftViewModel::class.java]

        binding.giftFriendName.text = friend.name
        binding.giftFriendPhone.text = friend.phone
        friend.imagePath?.let { imgPath ->
            GlideApp.with(this)
                .load(Firebase.storage.reference.child(imgPath))
                .into(binding.giftFriendImg)
        }

        // 선물 정보 등록
        resultAddGift =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.let {
                        val resultGift = it.getSerializableExtra("saveGift") as Gift
                        giftViewModel.saveGift(resultGift)
                    }
                }
            }

        // 선물 등록 버튼 클릭
        binding.fabGiftAdd.setOnClickListener {
            val addGiftIntent = Intent(this, GiftAddActivity::class.java)
            resultAddGift.launch(addGiftIntent)
        }

        // 선물 정보 수정
        if (intent.hasExtra("saveGift")) {
            val updateGift = intent.getSerializableExtra("saveGift") as Gift
            val oldGiftImagePath = intent.getStringExtra("oldGiftImagePath")
            giftViewModel.updateGift(updateGift, oldGiftImagePath)
        }

        // 선물 정보 삭제
        else if (intent.hasExtra("removeGift")) {
            val deleteGift = intent.getSerializableExtra("deleteGift") as Gift
            giftViewModel.removeGift(deleteGift)
        }

        binding.giftRecyclerView.layoutManager = LinearLayoutManager(view.context)
        binding.giftRecyclerView.setHasFixedSize(true)
        binding.giftRecyclerView.adapter = giftAdapter
    }

    /**
     * 선물 조회 다이얼로그 호출
     */
    override fun onGiftItemClicked(gift: Gift) {
        GiftDetailDialog(gift).show(supportFragmentManager, "giftDetailDialog")
    }

    override fun onResume() {
        super.onResume()
        giftViewModel.giftList.observe(this, { gift ->
            giftAdapter.setGiftList(gift)
        })
    }
}