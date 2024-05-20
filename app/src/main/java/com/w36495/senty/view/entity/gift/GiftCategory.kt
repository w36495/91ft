package com.w36495.senty.view.entity.gift

data class GiftCategory(
    val name: String
) {
    var id: String = ""
        private set

    fun setId(id: String) {
        this.id = id
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