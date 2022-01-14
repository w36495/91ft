package com.w36495.senty.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.w36495.senty.R
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.databinding.DialogGiftDetailBinding

class GiftDetailDialog(private val gift: Gift, private val position: Int) : DialogFragment() {

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

        if (gift.isReceived) {
            binding.giftDetailTopTitle.setText(R.string.title_giftReceive)
        } else {
            binding.giftDetailTopTitle.setText(R.string.title_giftGive)
        }

        binding.giftDetailDate.text = gift.giftDate
        binding.giftDetailTitle.text = gift.giftTitle
        binding.giftDetailMemo.text = gift.giftMemo

        binding.giftDetailUpdate.setOnClickListener {
            val intent = Intent(view.context, GiftAddActivity::class.java)
            intent.putExtra("giftPosition", position)
            intent.putExtra("giftType", gift.isReceived)
            intent.putExtra("giftDate", gift.giftDate)
            intent.putExtra("giftTitle", gift.giftTitle)
            intent.putExtra("giftMemo", gift.giftMemo)
            startActivity(intent)
            dismiss()
        }

        binding.giftDetailClose.setOnClickListener {
            dismiss()
        }

        binding.giftDetailRemove.setOnClickListener {
            MaterialAlertDialogBuilder(view.context)
                .setMessage(resources.getString(R.string.msg_gift_delete))
                // 취소 버튼
                .setNegativeButton(resources.getString(R.string.btn_cancel)) { _, _ ->
                    dismiss()
                }
                // 삭제 버튼
                .setPositiveButton(resources.getString(R.string.btn_delete)) { _, _ ->
                    Toast.makeText(view.context, "삭제버튼을 클릭하였습니다.", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
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