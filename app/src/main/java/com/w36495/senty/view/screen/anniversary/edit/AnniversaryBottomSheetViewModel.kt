package com.w36495.senty.view.screen.anniversary.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.screen.anniversary.contact.AnniversaryBottomSheetContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnniversaryBottomSheetViewModel @Inject constructor(
    private val anniversaryRepository: AnniversaryRepository,
) : ViewModel() {
    private val _effect = Channel<AnniversaryBottomSheetContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(AnniversaryBottomSheetContact.State())
    val state get() = _state.asStateFlow()

    fun handleEvent(event: AnniversaryBottomSheetContact.Event) {
        when (event) {
            AnniversaryBottomSheetContact.Event.OnClickSave -> {
                if (state.value.schedule.title.isEmpty()) {
                    _state.update { it.copy(isErrorTitle = true) }
                    return
                } else {
                    _state.update { it.copy(isErrorTitle = false) }
                }

                saveSchedule()
            }
            AnniversaryBottomSheetContact.Event.OnClickClose -> {

            }
            AnniversaryBottomSheetContact.Event.OnClickDelete -> {
                _state.update {
                    it.copy(showDeleteDialog = false)
                }
                removeSchedule()
            }
            AnniversaryBottomSheetContact.Event.OnClickUpdate -> {
                if (state.value.schedule.title.isEmpty()) {
                    _state.update { it.copy(isErrorTitle = true) }
                    return
                } else {
                    _state.update { it.copy(isErrorTitle = false) }
                }

                updateSchedule()
            }
            AnniversaryBottomSheetContact.Event.ShowCalendar -> {
                _state.update { it.copy(showCalendar = true) }
            }
            AnniversaryBottomSheetContact.Event.HideCalendar -> {
                _state.update { it.copy(showCalendar = false) }
            }
            AnniversaryBottomSheetContact.Event.ShowTimePicker -> {
                _state.update { it.copy(showTimePicker = true) }
            }
            AnniversaryBottomSheetContact.Event.HideTimePicker -> {
                _state.update { it.copy(showTimePicker = false) }
            }
            AnniversaryBottomSheetContact.Event.ShowDeleteDialog -> {
                _state.update { it.copy(showDeleteDialog = true) }
            }
            AnniversaryBottomSheetContact.Event.HideDeleteDialog -> {
                _state.update { it.copy(showDeleteDialog = false) }
            }
            is AnniversaryBottomSheetContact.Event.UpdateDate -> {
                val formattedDate = event.selectedDate
                    .toList()
                    .joinToString("-") { StringUtils.format2Digits(it) }

                _state.update { state ->
                    state.copy(
                        showCalendar = false,
                        selectedDate = event.selectedDate,
                        schedule = state.schedule.copy(date = formattedDate)
                    )
                }
            }
            is AnniversaryBottomSheetContact.Event.UpdateTitle -> {
                _state.update { state ->
                    state.copy(
                        schedule = state.schedule.copy(title = event.title),
                    )
                }
            }
            is AnniversaryBottomSheetContact.Event.UpdateLocation -> {
                _state.update { state ->
                    state.copy(
                        schedule = state.schedule.copy(location = event.location),
                    )
                }
            }
            is AnniversaryBottomSheetContact.Event.UpdateTime -> {
                _state.update { state ->
                    state.copy(
                        schedule = state.schedule.copy(time = event.time),
                        showTimePicker = false,
                    )
                }
            }
            is AnniversaryBottomSheetContact.Event.UpdateMemo -> {
                _state.update { state ->
                    state.copy(
                        schedule = state.schedule.copy(memo = event.memo),
                    )
                }
            }
            AnniversaryBottomSheetContact.Event.ResetTime -> {
                _state.update { state ->
                    state.copy(
                        schedule = state.schedule.copy(time = ""),
                        showTimePicker = false,
                    )
                }
            }
        }
    }

    fun getSchedule(scheduleId: String) {
        viewModelScope.launch {
            anniversaryRepository.getSchedule(scheduleId)
                .onSuccess {
                    _state.update { state ->
                        state.copy(
                            schedule = it.toUiModel()
                        )
                    }
                }
                .onFailure {
                    _effect.send(AnniversaryBottomSheetContact.Effect.ShowError(it))
                }
        }
    }

    private fun saveSchedule() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            anniversaryRepository.insertSchedule(state.value.schedule.toDomain())
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AnniversaryBottomSheetContact.Effect.ShowToast("등록 완료되었습니다."))
                    clearState()
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AnniversaryBottomSheetContact.Effect.ShowError(it))
                    clearState()
                }
        }
    }

    private fun updateSchedule() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            anniversaryRepository.patchSchedule(state.value.schedule.toDomain())
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AnniversaryBottomSheetContact.Effect.ShowToast("수정 완료되었습니다."))
                    clearState()
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AnniversaryBottomSheetContact.Effect.ShowError(it))
                    clearState()
                }
        }
    }

    private fun removeSchedule() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            anniversaryRepository.deleteSchedule(state.value.schedule.id)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AnniversaryBottomSheetContact.Effect.ShowToast("삭제 완료되었습니다."))
                    clearState()
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AnniversaryBottomSheetContact.Effect.ShowError(it))
                    clearState()
                }
        }
    }

    fun clearState() {
        _state.update { state ->
            state.copy(
                isLoading = false,
                schedule = state.schedule.copy(
                    title = "",
                    location = "",
                    date = "",
                    time = "",
                    memo = "",
                ),
                selectedDate = Triple(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth),
                showCalendar = false,
                showTimePicker = false,
                showDeleteDialog = false,
            )
        }
    }
}