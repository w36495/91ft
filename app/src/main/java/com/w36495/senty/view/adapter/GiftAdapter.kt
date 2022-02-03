package com.w36495.senty.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.view.listener.GiftSelectListener
import com.w36495.senty.R
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.databinding.GiftListGiveItemBinding
import com.w36495.senty.databinding.GiftListReceiveItemBinding
import com.w36495.senty.view.GlideApp

class GiftAdapter(
    private val context: Context,
    private val giftSelectListener: GiftSelectListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var giftList = listOf<Gift>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View

        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return if (viewType == GiftType.RECEIVE) {
            view = inflater.inflate(R.layout.gift_list_receive_item, parent, false)
            GiftReceiveHolder(GiftListReceiveItemBinding.bind(view))
        } else {
            view = inflater.inflate(R.layout.gift_list_give_item, parent, false)
            GiftGiveHolder(GiftListGiveItemBinding.bind(view))
        }
    }

    inner class GiftReceiveHolder(private val binding: GiftListReceiveItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun receiveGiftBind(gift: Gift) {
            binding.giftListImg.setImageResource(R.drawable.ic_launcher_background)
            binding.giftListDate.text = gift.date
            binding.giftListTitle.text = gift.title

            gift.imagePath?.let { imgPath ->
                GlideApp.with(binding.root)
                    .load(Firebase.storage.reference.child(imgPath))
                    .into(binding.giftListImg)
            }

            itemView.setOnClickListener {
                giftSelectListener.onGiftItemClicked(gift)
            }
        }

    }

    inner class GiftGiveHolder(private val binding: GiftListGiveItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun giveGiftBind(gift: Gift) {
            binding.giftListImg.setImageResource(R.drawable.ic_launcher_background)
            binding.giftListDate.text = gift.date
            binding.giftListTitle.text = gift.title

            gift.imagePath?.let { imgPath ->
                GlideApp.with(binding.root)
                    .load(Firebase.storage.reference.child(imgPath))
                    .into(binding.giftListImg)
            }

            itemView.setOnClickListener {
                giftSelectListener.onGiftItemClicked(gift)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GiftReceiveHolder) {
            holder.receiveGiftBind(giftList[position])
        } else if (holder is GiftGiveHolder) {
            holder.giveGiftBind(giftList[position])
        }
    }

    override fun getItemCount(): Int = giftList.size

    fun setGiftList(giftList: List<Gift>) {
        this.giftList = giftList
        notifyDataSetChanged()
    }

    /**
     * 받은/보낸 선물에 따라 viewType 설정
     */
    override fun getItemViewType(position: Int): Int {
        return if (giftList[position].received) {
            GiftType.RECEIVE
        } else {
            GiftType.GIVE
        }
    }
}