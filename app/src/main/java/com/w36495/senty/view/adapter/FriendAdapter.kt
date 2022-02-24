package com.w36495.senty.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.w36495.senty.R
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.databinding.FriendListItemBinding
import com.w36495.senty.view.GlideApp
import com.w36495.senty.view.listener.FriendSelectListener

class FriendAdapter(private val friendSelectListener: FriendSelectListener) :
    RecyclerView.Adapter<FriendAdapter.FriendHolder>() {

    private var friendList: List<Friend> = listOf()

    inner class FriendHolder(private val binding: FriendListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: Friend) {
            binding.friendListName.text = friend.name
            binding.friendItemImg.setImageResource(R.drawable.bg_friend_basic_image)

            friend.imagePath?.let { imagePath ->
                GlideApp.with(binding.root)
                    .load(Firebase.storage.reference.child(imagePath))
                    .into(binding.friendItemImg)
            }

            itemView.setOnClickListener {
                friendSelectListener.onFriendInfoClicked(friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friend_list_item, parent, false)
        return FriendHolder(FriendListItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int = friendList.size

    fun setFriendList(friendList: List<Friend>) {
        this.friendList = friendList
        notifyDataSetChanged()
    }

}