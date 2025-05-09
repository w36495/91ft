package com.w36495.senty.view.screen.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendSelectionViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
) : ViewModel() {
    val state = friendRepository.friends
        .map {
            val mappedFriends = it.map { friend -> friend.toUiModel() }

            FriendSelectionUiState.Success(mappedFriends)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FriendSelectionUiState.Loading
        )

    fun refresh() {
        viewModelScope.launch {
            friendRepository.fetchFriends()
                .onFailure {
                    Log.d("FriendSelectionVM", it.stackTraceToString())
                }
        }
    }
}

sealed interface FriendSelectionUiState {
    data object Loading : FriendSelectionUiState
    data class Success(val data: List<FriendUiModel>) : FriendSelectionUiState
}