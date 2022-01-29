package com.w36495.senty.view

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.R
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.ActivityGiftAddBinding
import com.w36495.senty.view.adapter.GlideApp
import java.util.*

class GiftAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiftAddBinding

    private var isReceive = true
    private var isUpdate = false // 선물 등록 : false, 선물 수정 : true

    private lateinit var giftKey: String
    private var giftImageUri: Uri? = null
    private lateinit var oldGiftImagePath: String

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

            GlideApp.with(binding.root)
                .load(Firebase.storage.reference.child(updateGift.giftImagePath!!))
                .into(binding.giftAddImg)

            oldGiftImagePath = updateGift.giftImagePath!!

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

        binding.giftAddImgBtn.setOnClickListener {
            getImageByGallery()
        }

        // 날짜 선택버튼 클릭
        binding.giftAddDateBtn.setOnClickListener {
            showDateDialog()
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
                binding.giftAddMemo.text.toString(),
                giftImageUri.toString()
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

    // TODO 카메라로부터 사진 가져오기
    private fun selectCamera() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_DENIED) {
            // 권한이 없어서 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 777)
        }
    }

    private fun getImageByGallery() {
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 888)
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            resultGalleryImage.launch(galleryIntent)
        }
    }

    private fun setImageByGallery(result: ActivityResult) {
        giftImageUri = result.data?.data!!
        Glide.with(this).load(giftImageUri).into(binding.giftAddImg)
    }

}