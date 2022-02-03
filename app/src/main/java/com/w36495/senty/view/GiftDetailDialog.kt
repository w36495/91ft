package com.w36495.senty.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.R
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.DialogGiftDetailBinding

class GiftDetailDialog(private val gift: Gift) : DialogFragment() {

    private lateinit var binding: DialogGiftDetailBinding

    private lateinit var size: Point

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogGiftDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        // dialog 설정
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // device size
        val windowManager = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        size = Point()
        display.getSize(size)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gift.imagePath?.let { imagePath ->
            GlideApp.with(view)
                .load(Firebase.storage.reference.child(imagePath))
                .into(binding.giftDetailImg)
        }

        if (gift.received) {
            binding.giftDetailTopTitle.setText(R.string.title_giftReceive)
        } else {
            binding.giftDetailTopTitle.setText(R.string.title_giftGive)
        }

        binding.giftDetailDate.text = gift.date
        binding.giftDetailTitle.text = gift.title
        binding.giftDetailMemo.text = gift.memo

        // 선물 수정 버튼 클릭
        binding.giftDetailUpdate.setOnClickListener {
            updateGift(view)
            dismiss()
        }

        // 닫기 버튼 클릭
        binding.giftDetailClose.setOnClickListener {
            dismiss()
        }

        // 선물 삭제 버튼 클릭
        binding.giftDetailRemove.setOnClickListener {
            MaterialAlertDialogBuilder(view.context)
                .setMessage(resources.getString(R.string.msg_gift_delete))
                // 취소 버튼
                .setNegativeButton(resources.getString(R.string.btn_cancel)) { _, _ ->
                    dismiss()
                }
                // 삭제 버튼
                .setPositiveButton(resources.getString(R.string.btn_delete)) { _, _ ->
                    removeGift(view)
                    dismiss()
                }
                .show()
        }
    }

    /**
     * 선물 정보 수정
     */
    private fun updateGift(view: View) {
        val updateGiftIntent = Intent(view.context, GiftAddActivity::class.java)
        updateGiftIntent.putExtra("updateGift", gift)
        startActivity(updateGiftIntent)
    }

    /**
     * 선물 정보 삭제
     */
    private fun removeGift(view: View) {
        val removeGiftIntent = Intent(view.context, GiftListActivity::class.java)
        removeGiftIntent.putExtra("removeGift", gift)
        startActivity(removeGiftIntent)
    }

    override fun onResume() {
        super.onResume()
        // 디바이스 크기 -> 다이얼로그 크기 정하기
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}