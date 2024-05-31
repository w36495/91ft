package com.w36495.senty.view.entity.gift

import com.w36495.senty.data.domain.GiftCategoryEntity
import kotlinx.serialization.Serializable

@Serializable
data class GiftCategory(
    val name: String
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
    }

    fun toDataEntity() = GiftCategoryEntity(name = this@GiftCategory.name)

    fun copy() = GiftCategory (name = this@GiftCategory.name).apply {
        setId(this@GiftCategory.id)
    }

    override fun toString(): String {
        return "GiftCategory(name=$name, id=$id)"
    }

    companion object {
        val DEFAULT_CATEGORY = listOf(
            GiftCategory("취업"),
            GiftCategory("졸업"),
            GiftCategory("생일"),
            GiftCategory("기타")
        )

        val emptyCategory = GiftCategory(name = "")
    }
}