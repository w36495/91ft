package com.w36495.senty.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.ActivityGiftListBinding
import com.w36495.senty.view.adapter.GiftAdapter
import com.w36495.senty.viewModel.GiftListViewModel

class GiftListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiftListBinding
    private lateinit var giftListViewModel: GiftListViewModel

    private lateinit var giftAdapter: GiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiftListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        giftListViewModel = ViewModelProvider(this)[GiftListViewModel::class.java]

        giftAdapter = GiftAdapter(this)

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


    }
}