package com.w36495.senty.view.screen.gift.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.usecase.DeleteGiftCategoryUseCase
import com.w36495.senty.domain.usecase.UpdateGiftCategoryUseCase
import com.w36495.senty.view.screen.gift.category.contact.GiftCategoryContact
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class GiftCategoriesViewModel @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository,
    private val deleteGiftCategoryUseCase: DeleteGiftCategoryUseCase,
    private val updateGiftCategoryUseCase: UpdateGiftCategoryUseCase,
) : ViewModel() {
    private val mutex = Mutex()

    private val _effect = Channel<GiftCategoryContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(GiftCategoryContact.State())
    val state get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            giftCategoryRepository.fetchCategories()
                .onSuccess {
                    giftCategoryRepository.categories
                        .onStart {
                            _state.update {
                                it.copy(isLoading = true)
                            }
                        }
                        .map { giftCategories ->
                            giftCategories.map { it.toUiModel() }
                        }
                        .collectLatest { collectData ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    categories = collectData,
                                )
                            }
                        }
                }
                .onFailure {
                    _state.update { state -> state.copy(isLoading = false) }
                    Log.d("GiftCategoryVM", it.stackTraceToString())
                    sendEffect(GiftCategoryContact.Effect.ShowError(it))
                }
        }
    }

    fun handleEvent(event: GiftCategoryContact.Event) {
        when (event) {
            is GiftCategoryContact.Event.OnClickAdd -> {
                viewModelScope.launch {
                    if (state.value.showAddCategoryDialog) {
                        _state.update {
                            it.copy(showAddCategoryDialog = false)
                        }

                        event.category?.let { newGiftCategory ->
                            addGiftCategory(newGiftCategory)
                        }
                    } else {
                        _state.update {
                            it.copy(showAddCategoryDialog = true)
                        }
                    }
                }
            }
            GiftCategoryContact.Event.OnClickBack -> {
                sendEffect(GiftCategoryContact.Effect.NavigateToSettings)
            }
            is GiftCategoryContact.Event.OnClickEdit -> {
                viewModelScope.launch {
                    event.category?.let {
                        _state.update { state ->
                            state.copy(
                                selectedCategory = it,
                                showEditCategoryDialog = !state.showEditCategoryDialog,
                            )
                        }
                    } ?: run {
                        _state.update { state ->
                            state.copy(
                                showEditCategoryDialog = !state.showEditCategoryDialog,
                                selectedCategory = null,
                            )
                        }
                    }
                }
            }
            is GiftCategoryContact.Event.OnSelectEdit -> {
                _state.update { state ->
                    state.copy(selectedCategory = event.category)
                }

                editGiftCategory(event.category)
            }
            is GiftCategoryContact.Event.OnClickDelete -> {
                viewModelScope.launch {
                    event.category?.let {
                        _state.update { state ->
                            state.copy(
                                selectedCategory = it,
                                showDeleteCategoryDialog = !state.showDeleteCategoryDialog,
                            )
                        }
                    } ?: run {
                        _state.update { state ->
                            state.copy(
                                showDeleteCategoryDialog = !state.showDeleteCategoryDialog,
                                selectedCategory = null,
                            )
                        }
                    }
                }
            }
            is GiftCategoryContact.Event.OnSelectDelete -> {
                state.value.selectedCategory?.let {
                    deleteGiftCategory(it)
                }
            }
        }
    }


    private fun sendEffect(effect: GiftCategoryContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun addGiftCategory(category: GiftCategoryUiModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = mutex.withLock { giftCategoryRepository.insertCategory(category.toDomain()) }

            result
                .onSuccess {
                    _state.update { it.copy(isLoading = false, showAddCategoryDialog = false) }
                    sendEffect(GiftCategoryContact.Effect.ShowToast("등록 완료되었습니다."))
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, showAddCategoryDialog = false) }
                    sendEffect(GiftCategoryContact.Effect.ShowError(it))
                }
        }
    }

    private fun editGiftCategory(category: GiftCategoryUiModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = updateGiftCategoryUseCase(category.toDomain())

            result
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedCategory = null,
                            showEditCategoryDialog = false,
                        )
                    }
                    sendEffect(GiftCategoryContact.Effect.ShowToast("수정 완료되었습니다."))
                    giftCategoryRepository.fetchCategories()
                }
                .onFailure {
                    Log.d("GiftCategoryVM", it.stackTraceToString())
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            showEditCategoryDialog = false,
                            selectedCategory = null,
                        )
                    }
                    sendEffect(GiftCategoryContact.Effect.ShowError(it))
                }

        }
    }

    private fun deleteGiftCategory(category: GiftCategoryUiModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = deleteGiftCategoryUseCase(category.toDomain())

            result
                .onSuccess {
                    _state.update { it.copy(isLoading = false, showDeleteCategoryDialog = false) }
                    sendEffect(GiftCategoryContact.Effect.ShowToast("삭제 완료되었습니다."))
                }
                .onFailure {
                    Log.d("GiftCategoryVM", it.stackTraceToString())
                    _state.update { it.copy(isLoading = false, showDeleteCategoryDialog = false) }
                    sendEffect(GiftCategoryContact.Effect.ShowError(it))
                }
        }
    }
}