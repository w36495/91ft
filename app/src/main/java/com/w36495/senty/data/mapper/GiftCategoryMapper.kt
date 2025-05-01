package com.w36495.senty.data.mapper

import com.w36495.senty.data.domain.GiftCategoryEntity
import com.w36495.senty.domain.entity.GiftCategory
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel

fun GiftCategoryEntity.toDomain(id: String): GiftCategory {
    return GiftCategory(
        id = id,
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

fun GiftCategory.toUiModel(): GiftCategoryUiModel {
    return GiftCategoryUiModel(
        id = this.id,
        name = this.name,
    )
}

fun GiftCategory.toEntity(): GiftCategoryEntity {
    return GiftCategoryEntity(
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

fun GiftCategoryUiModel.toDomain(): GiftCategory {
    return GiftCategory(
        id = this.id,
        name = this.name,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
    )
}