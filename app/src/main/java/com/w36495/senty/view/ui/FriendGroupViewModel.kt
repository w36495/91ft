package com.w36495.senty.view.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.FriendGroupRepository
import com.w36495.senty.view.entity.FriendGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            try {
                val result = friendGroupRepository.getFriendGroups()
                _friendGroups.update { result.map { it.toDomainModel() } }
            } catch (exception: Exception) {
                Log.d("FriendGroupViewModel", exception.printStackTrace().toString())
            }
        }
    }
}