package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.view.entity.FriendGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendGroupViewModel @Inject constructor(
    private val friendGroupRepository: FriendGroupRepository
) : ViewModel() {
    private var _friendGroups = MutableStateFlow<List<FriendGroup>>(emptyList())
    val friendGroups = _friendGroups.asStateFlow()

    init {
        viewModelScope.launch {
            friendGroupRepository.getFriendGroups()
                .catch {
                    Log.d("FriendGroupViewModel", it.message.toString())
                }
                .map { groups ->
                    groups.map { it.toDomainModel() }
                }
                .collect {
                    _friendGroups.value = it
                }
        }
    }

    fun saveFriendGroup(friendGroup: FriendGroup) {
        viewModelScope.launch {
            friendGroupRepository.insertFriendGroup(friendGroup.toDataEntity())
        }
    }
}