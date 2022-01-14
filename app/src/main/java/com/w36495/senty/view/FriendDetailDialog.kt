package com.w36495.senty.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.w36495.senty.R
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.DialogFriendDetailBinding

class FriendDetailDialog(private val friend: Friend, private val position: Int) : DialogFragment() {

    private lateinit var binding: DialogFriendDetailBinding
    private lateinit var size: Point

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFriendDetailBinding.inflate(inflater, container, false)
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
        binding.friendDetailImg.setImageResource(R.drawable.ic_launcher_background)
        binding.friendDetailName.text = friend.name
        binding.friendDetailPhone.text = friend.phone

        // 선택된 친구의 선물 목록으로 이동
        binding.friendDetailGift.setOnClickListener {
            val intent = Intent(view.context, GiftListActivity::class.java)
            intent.putExtra("friendName", friend.name)
            intent.putExtra("friendPhone", friend.phone)
            startActivity(intent)
            dismiss()
        }

        // 다이얼로그 창 닫기(엑스버튼) 클릭
        binding.friendDetailClose.setOnClickListener {
            dismiss()
        }

        // 선택된 친구의 정보를 수정할 때 -> 수정 화면으로 이동
        binding.friendDetailUpdate.setOnClickListener {
            val intent = Intent(view.context, FriendAddActivity::class.java)
            intent.putExtra("friendName", friend.name)
            intent.putExtra("friendPhone", friend.phone)
            intent.putExtra("friendPosition", position)
            startActivity(intent)
            dismiss()
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