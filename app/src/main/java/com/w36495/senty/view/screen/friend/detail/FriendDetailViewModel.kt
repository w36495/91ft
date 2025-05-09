package com.w36495.senty.view.screen.friend.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.manager.CachedImageInfoManager
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toFriendDetailUiModel
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.domain.usecase.DeleteFriendUseCase
import com.w36495.senty.view.screen.friend.detail.contact.FriendDetailContact
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImageRepository,
    private val deleteFriendUseCase: DeleteFriendUseCase,
) : ViewModel() {
    private val _effect = Channel<FriendDetailContact.Effect>(capacity = Channel.CONFLATED)
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(FriendDetailContact.State(isLoading = true))
    val state get() = _state.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            combine(
                friendRepository.friends,
                giftRepository.gifts
            ) { friends, allGift ->
                val friend = friends.first { it.id == friendId }.toUiModel()
                val gifts = allGift.filter { it.friendId == friendId }.map { it.toFriendDetailUiModel() }

                friend to gifts
            }
                .onStart {
                    _state.update { state -> state.copy(isLoading = true) }
                }
                .catch {
                    _state.update { state -> state.copy(isLoading = false) }
                    _effect.send(FriendDetailContact.Effect.ShowError(it))
                }
                .collect { (friend, gifts) ->
                    val enrichedGifts = gifts.map { gift ->
                        gift.thumbnail?.let { thumbnailName ->
                            CachedImageInfoManager.get(thumbnailName)?.let {
                                gift.copy(thumbnailPath = it)
                            } ?: run {
                                giftImageRepository.getGiftThumbs(gift.id, thumbnailName)
                                    .map { path ->
                                        CachedImageInfoManager.put(thumbnailName, path)
                                        gift.copy(thumbnailPath = path)
                                    }
                                    .getOrElse { gift }
                            }
                        } ?: gift
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            friend = friend,
                            gifts = enrichedGifts
                        )
                    }
                }
        }
    }

    fun handleEvent(event: FriendDetailContact.Event) {
        when (event) {
            FriendDetailContact.Event.OnClickBack -> {
                sendEffect(FriendDetailContact.Effect.NavigateToFriends)
            }
            FriendDetailContact.Event.OnClickDelete -> {
                _state.update { state ->
                    state.copy(showDeleteDialog = true)
                }
            }
            is FriendDetailContact.Event.OnClickGift -> {
                sendEffect(FriendDetailContact.Effect.NavigateToGiftDetail(event.giftId))
            }
            is FriendDetailContact.Event.OnClickEdit -> {
                sendEffect(FriendDetailContact.Effect.NavigateToEditFriend(event.friendId))
            }
            is FriendDetailContact.Event.OnSelectDelete -> {
                _state.update { state ->
                    state.copy(showDeleteDialog = false)
                }

                event.friendId?.let {
                    removeFriend(state.value.friend)
                }
            }
        }
    }

    fun sendEffect(effect: FriendDetailContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun removeFriend(friend: FriendUiModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            deleteFriendUseCase(friend.toDomain())
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(FriendDetailContact.Effect.ShowToast("삭제 완료되었습니다."))
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(FriendDetailContact.Effect.ShowError(it))
                }
        }
    }
}