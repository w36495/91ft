package com.w36495.senty.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.R
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
    private lateinit var progressDialog: ProgressDialog

    private lateinit var friend: Friend // 친구의 정보 저장 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGiftListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        MobileAds.initialize(this) {}
        binding.adView.loadAd(AdRequest.Builder().build())

        // 친구의 정보를 클릭했을 때 저장했던 정보 불러오기
        val sharedPref = getSharedPreferences("friend", Context.MODE_PRIVATE)
        friend = Friend(
            sharedPref.getString("friendKey", "")!!,
            sharedPref.getString("friendName", "")!!,
            sharedPref.getString("friendImagePath", null)
        )

        giftAdapter = GiftAdapter(view.context, this)
        giftViewModel = ViewModelProvider(
            this,
            GiftViewModel.GiftViewModelFactory(friend.key)
        )[GiftViewModel::class.java]
        progressDialog = ProgressDialog(this)

        binding.giftListName.text = friend.name
        friend.imagePath?.let { imgPath ->
            GlideApp.with(this)
                .load(Firebase.storage.reference.child(imgPath))
                .into(binding.giftListImg)
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

        // 뒤로가기 버튼 클릭
        binding.giftListToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // 홈 버튼 클릭
        binding.giftListToolbar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.home -> {
                    val moveFriendListIntent = Intent(this, FriendListActivity::class.java)
                    moveFriendListIntent.flags = FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(moveFriendListIntent)
                    finish()
                    true
                }
                else -> false
            }
        }

        // 선물 정보 수정
        if (intent.hasExtra("saveGift")) {
            val updateGift = intent.getSerializableExtra("saveGift") as Gift
            val oldGiftImagePath = intent.getStringExtra("oldGiftImagePath")
            giftViewModel.updateGift(updateGift, oldGiftImagePath)
        }

        // 선물 정보 삭제
        else if (intent.hasExtra("removeGift")) {
            val deleteGift = intent.getSerializableExtra("removeGift") as Gift
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
        giftViewModel.giftList.observe(this) { gift ->
            giftAdapter.setGiftList(gift)
        }
        giftViewModel.giftProgress.observe(this) { progress ->
            showProgressDialog(progress)
        }
        giftViewModel.giftToast.observe(this) { exception ->
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