package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.w36495.senty.R
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.ActivityGiftAddBinding

class GiftAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiftAddBinding

    private var isReceive = true
    private var isUpdate = false // 선물 등록 : false, 선물 수정 : true

    private lateinit var giftKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGiftAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 선물 조회 다이얼로그를 통해 넘겨받은 선물 정보 셋팅
        if (intent.hasExtra("updateGift")) {
            isUpdate = true
            val updateGift = intent.getSerializableExtra("updateGift") as Gift

            if (updateGift.received) {
                binding.giftAddTypeReceive.isChecked = true
            } else {
                binding.giftAddTypeGive.isChecked = true
            }

            giftKey = updateGift.giftKey
            binding.giftAddDate.setText(updateGift.giftDate)
            binding.giftAddTitle.setText(updateGift.giftTitle)
            binding.giftAddMemo.setText(updateGift.giftMemo)
        }

        // 등록 버튼 클릭 시
        binding.giftAddSave.setOnClickListener {
            val intent = Intent(this, GiftListActivity::class.java)

            when (binding.giftAddType.checkedRadioButtonId) {
                R.id.gift_add_type_receive -> isReceive = true
                R.id.gift_add_type_give -> isReceive = false
            }
            val gift = Gift(
                if (isUpdate) giftKey else "",
                isReceive,
                binding.giftAddDate.text.toString(),
                binding.giftAddTitle.text.toString(),
                binding.giftAddMemo.text.toString()
            )

            intent.putExtra("saveGift", gift)

            if (isUpdate) {
                startActivity(intent)
                finish()
            } else {
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}