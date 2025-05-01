package com.w36495.senty.data.manager

object CachedImageInfoManager {
    private val cachedImageInfo = hashMapOf<String, String>()

    fun get(imageName: String): String? {
        return if (cachedImageInfo.containsKey(imageName)) {
            cachedImageInfo.getValue(imageName)
        } else null
    }

    fun put(imageName: String, path: String) {
        cachedImageInfo[imageName] = path
    }

    fun remove(imageName: String) {
        cachedImageInfo.remove(imageName)
    }

    fun clear() {
        cachedImageInfo.clear()
    }
}