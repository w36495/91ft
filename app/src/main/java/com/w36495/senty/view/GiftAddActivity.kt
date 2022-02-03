package com.w36495.senty.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.R
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.ActivityGiftAddBinding
import java.util.*

class GiftAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiftAddBinding

    private var isReceive = true
    private var isUpdate = false // 선물 등록 : false, 선물 수정 : true

    private lateinit var giftKey: String
    private var giftImageUri: Uri? = null
    private var oldGiftImagePath: String? = null

    private lateinit var resultGalleryImage: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGiftAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 선물 조회 다이얼로그를 통해 넘겨받은 선물 정보 셋팅
        if (intent.hasExtra("updateGift")) {
            isUpdate = true
            val updateGift = intent.getSerializableExtra("updateGift") as Gift

            updateGift.imagePath?.let { imagePath ->
                GlideApp.with(binding.root)
                    .load(Firebase.storage.reference.child(imagePath))
                    .into(binding.giftAddImg)
            }

            oldGiftImagePath = updateGift.imagePath

            if (updateGift.received) {
                binding.giftAddTypeReceive.isChecked = true
            } else {
                binding.giftAddTypeGive.isChecked = true
            }

            giftKey = updateGift.key
            binding.giftAddDate.setText(updateGift.date)
            binding.giftAddTitle.setText(updateGift.title)
            binding.giftAddMemo.setText(updateGift.memo)
        }

        // 선물 이미지 선택 버튼 클릭
        binding.giftAddImgBtn.setOnClickListener {
            ImagePermission().getImageByGallery(view, resultGalleryImage)
        }

        // 날짜 선택버튼 클릭
        binding.giftAddDateBtn.setOnClickListener {
            showDateDialog()
        }

        // 등록 버튼 클릭
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
                binding.giftAddMemo.text.toString(),
                if (isUpdate) {
                    giftImageUri?.toString() ?: oldGiftImagePath
                } else giftImageUri?.toString()
            )

            intent.putExtra("saveGift", gift)

            if (isUpdate) {
                intent.putExtra("oldGiftImagePath", oldGiftImagePath)
                startActivity(intent)
                finish()
            } else {
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        resultGalleryImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    setImageByGallery(result)
                }
            }
    }

    // 날짜 선택이 가능한 달력 다이얼로그 호출
    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val mYear = calendar.get(Calendar.YEAR)
        val mMonth = calendar.get(Calendar.MONTH)
        val mDay = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            lateinit var printMonth: String
            lateinit var printDayOfMonth: String
            // 월 2자리 표현
            if ((month + 1) in 1..9) {
                printMonth = "0${month + 1}"
            } else {
                printMonth = (month + 1).toString()
            }
            // 일 2자리 표현
            if (dayOfMonth in 1..9) {
                printDayOfMonth = "0$dayOfMonth"
            } else {
                printDayOfMonth = dayOfMonth.toString()
            }
            binding.giftAddDate.setText("${year}/${printMonth}/${printDayOfMonth}")
        }
        DatePickerDialog(this, datePickerDialog, mYear, mMonth, mDay).show()
    }

    private fun setImageByGallery(result: ActivityResult) {
        giftImageUri = result.data?.data!!
        Glide.with(this).load(giftImageUri).into(binding.giftAddImg)
    }

}