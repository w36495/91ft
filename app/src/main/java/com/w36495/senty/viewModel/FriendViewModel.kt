package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository
) : ViewModel() {
    private val _friends = MutableStateFlow<List<FriendEntity>>(emptyList())
    val friends = _friends.asStateFlow()

    init {
        viewModelScope.launch {
            friendGroupRepository.getFriendGroups().combine(
                friendRepository.getFriends()
            ) {
                friendGroups, friends ->

                friends.map { friend ->
                    val group = friendGroups.filter {
                        it.id == friend.groupId
                    }

                    friend.toDomainEntity().also {
                        it.setFriendGroup(group[0].toDomainModel())
                    }
                }
            }
                .collect {
                    _friends.value = it
                }
        }
    }

    fun saveFriend(friend: FriendEntity) {
        viewModelScope.launch {
            val result = friendRepository.insertFriend(friend.toDataEntity())

            if (result.isSuccessful) {
                result.body()?.let {
                    val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                    val key = jsonObject["name"].toString().replace("\"", "")

                    val friendKeyResult = friendRepository.patchFriendId(key)
                    if (friendKeyResult.isSuccessful) {
                        if (friendKeyResult.body()?.string() == it.string()) {
                            // TODO : 등록 성공
                        }
                    } else {
                        // TODO : 등록 실패
                    }
                }
            } else {
                Log.d("FriendViewModel", result.errorBody().toString())
            }
        }
    }
}