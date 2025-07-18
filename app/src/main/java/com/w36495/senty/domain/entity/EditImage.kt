package com.w36495.senty.domain.entity

sealed interface EditImage {
    data class Original(val path: String) : EditImage
    data class New(val byteArray: ByteArray) : EditImage {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as New

            return byteArray.contentEquals(other.byteArray)
        }

        override fun hashCode(): Int {
            return byteArray.contentHashCode()
        }
    }
}