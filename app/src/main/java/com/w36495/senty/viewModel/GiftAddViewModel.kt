package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.gift.GiftEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@HiltViewModel
class GiftAddViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
) : ViewModel() {
    fun saveGift(gift: GiftEntity) {
        viewModelScope.launch {
            val result = giftRepository.insertGift(gift)

            if (result.isSuccessful) {
                result.body()?.let {
                    val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                    val key = jsonObject["name"].toString().replace("\"", "")

                    val giftKeyResult = giftRepository.patchGiftKey(key)
                    if (giftKeyResult.isSuccessful) {
                        if (giftKeyResult.body()?.string() == it.string()) { }
                    } else { }
                }
            } else {
                Log.d("GiftAddVM", result.errorBody().toString())
            }
        }
    }
}