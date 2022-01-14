package com.w36495.senty.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.w36495.senty.GiftSelectListener
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.ActivityGiftListBinding
import com.w36495.senty.view.adapter.GiftAdapter
import com.w36495.senty.viewModel.GiftListViewModel

class GiftListActivity : AppCompatActivity(), GiftSelectListener {

    private lateinit var binding: ActivityGiftListBinding
    private lateinit var giftListViewModel: GiftListViewModel

    private lateinit var giftAdapter: GiftAdapter

    private lateinit var resultGiftInfo: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiftListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        giftListViewModel = ViewModelProvider(this)[GiftListViewModel::class.java]

        giftAdapter = GiftAdapter(this, this)

        val giftList = arrayListOf<Gift>()
        giftList.add(Gift(true, "2020/12/01", "생일선물", "기분이조타"))
        giftList.add(Gift(false, "2020/12/01", "생일선물", "기분이조타"))
        giftList.add(Gift(true, "2020/12/01", "생일선물", "기분이조타"))
        giftList.add(Gift(true, "2020/12/01", "생일선물", "기분이조타"))
        giftList.add(Gift(false, "2020/12/01", "생일선물", "기분이조타"))


        if (intent.hasExtra("friendName")) {
            binding.giftFriendName.text = intent.getStringExtra("friendName")
            binding.giftFriendPhone.text = intent.getStringExtra("friendPhone")
        }

        giftAdapter.setGiftList(giftList)

        binding.giftRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.giftRecyclerView.setHasFixedSize(true)
        binding.giftRecyclerView.adapter = giftAdapter

        resultGiftInfo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val giftDate = result.data?.getStringExtra("giftDate")?:""
                val giftTitle = result.data?.getStringExtra("giftTitle")?:""
                val giftMemo = result.data?.getStringExtra("giftMemo")?:""
                val giftType = result.data?.getBooleanExtra("giftType", false)
                giftListViewModel.addGift(Gift(giftType!!, giftDate, giftTitle, giftMemo))
            }
        }

        binding.fabGiftAdd.setOnClickListener {
            val intent = Intent(this, GiftAddActivity::class.java)
            resultGiftInfo.launch(intent)
        }

        giftListViewModel.giftList.observe(this, { gift ->
            giftAdapter.setGiftList(gift)
        })

        if (intent.hasExtra("giftPosition")) {
            giftListViewModel.updateGift(
                Gift(intent.getBooleanExtra("giftType", false),
                    intent.getStringExtra("giftDate").toString(),
                    intent.getStringExtra("giftTitle").toString(),
                    intent.getStringExtra("giftMemo").toString()),
                intent.getIntExtra("giftPosition", -1)
            )
        }


    }

    override fun onGiftItemClicked(gift: Gift, position: Int) {
        GiftDetailDialog(gift, position).show(supportFragmentManager, "giftDetailDialog")
    }

}