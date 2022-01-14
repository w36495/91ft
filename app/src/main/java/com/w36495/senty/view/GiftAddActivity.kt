package com.w36495.senty.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.w36495.senty.R
import com.w36495.senty.databinding.ActivityGiftAddBinding

class GiftAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiftAddBinding
    private var isReceive = true
    private var isUpdate = false // 선물 등록 : false, 선물 수정 : true
    private var giftPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiftAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra("giftType")) {
            if (intent.hasExtra("giftType")) {
                binding.giftAddTypeReceive.isChecked = true
            } else binding.giftAddTypeGive.isChecked = true
            binding.giftAddDate.setText(intent.getStringExtra("giftDate"))
            binding.giftAddTitle.setText(intent.getStringExtra("giftTitle"))
            binding.giftAddMemo.setText(intent.getStringExtra("giftMemo"))
            giftPosition = intent.getIntExtra("giftPosition", -1)
            isUpdate = true
        }


        binding.giftAddSave.setOnClickListener {
            val intent = Intent(this, GiftListActivity::class.java)
            intent.putExtra("giftDate", binding.giftAddDate.text.toString())
            intent.putExtra("giftTitle", binding.giftAddTitle.text.toString())
            intent.putExtra("giftMemo", binding.giftAddMemo.text.toString())
            when (binding.giftAddType.checkedRadioButtonId) {
                R.id.gift_add_type_receive -> isReceive = true
                R.id.gift_add_type_give -> isReceive = false
            }
            intent.putExtra("giftType", isReceive)

            if (isUpdate) {
                intent.putExtra("giftPosition", giftPosition)
                startActivity(intent)
            } else {
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    }
}