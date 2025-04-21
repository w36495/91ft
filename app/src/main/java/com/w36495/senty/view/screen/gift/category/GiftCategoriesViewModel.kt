package com.w36495.senty.view.screen.gift.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.GiftCategoryRepository
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
import javax.inject.Inject

@HiltViewModel
class GiftCategoriesViewModel @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository
) : ViewModel() {
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
                    _state.update {
                        it.copy(isLoading = false)
                    }
                    Log.d("GiftCategoryVM", it.stackTraceToString())
                    sendEffect(GiftCategoryContact.Effect.ShowError("오류가 발생하였습니다."))
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
                    if (state.value.showEditCategoryDialog) {
                        // 보여진 ㅏㅇ태 + 취소
                        event.category?.let { editGiftCategory ->
                            _state.update {
                                it.copy(
                                    showEditCategoryDialog = false,
                                )
                            }
                            editGiftCategory(editGiftCategory)
                        } ?: run {
                            _state.update {
                                it.copy(
                                    selectedCategory = null,
                                    showEditCategoryDialog = false,
                                )
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                selectedCategory = event.category,
                                showEditCategoryDialog = true,
                            )
                        }
                    }
                }
            }
            is GiftCategoryContact.Event.OnClickDelete -> {
                viewModelScope.launch {
                    if (state.value.showDeleteCategoryDialog) {
                        event.categoryId?.let {
                            _state.update { state ->
                                state.copy(
                                    showDeleteCategoryDialog = false,
                                )
                            }
                            deleteGiftCategory(it)
                        } ?: run {
                            _state.update {
                                it.copy(
                                    selectedCategory = null,
                                    showDeleteCategoryDialog = false,
                                )
                            }
                        }


                    } else {
                        _state.update {
                            it.copy(showDeleteCategoryDialog = true)
                        }
                    }
                }
            }
            is GiftCategoryContact.Event.OnClickSave -> {

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
            _state.update {
                it.copy(isLoading = true)
            }

            val result = giftCategoryRepository.insertCategory(category.toDomain())

            result
                .onSuccess { 
                    sendEffect(GiftCategoryContact.Effect.ShowToast("등록 완료되었습니다."))
                }
                .onFailure {
                    sendEffect(GiftCategoryContact.Effect.ShowError("오류가 발생하였습니다."))
                }
        }
    }

    private fun editGiftCategory(category: GiftCategoryUiModel) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    selectedCategory = null,
                )
            }

            val result = giftCategoryRepository.updateCategory(category.toDomain())

            result
                .onSuccess {
                    sendEffect(GiftCategoryContact.Effect.ShowToast("수정 완료되었습니다."))
                }
                .onFailure {
                    sendEffect(GiftCategoryContact.Effect.ShowError("오류가 발생하였습니다."))
                }

        }
    }

    private fun deleteGiftCategory(categoryId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    selectedCategory = null,
                )
            }
        }
    }
}