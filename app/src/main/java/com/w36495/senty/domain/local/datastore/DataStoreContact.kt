package com.w36495.senty.domain.local.datastore

interface DataStoreContact<T> {
    suspend fun save(value: T)

    suspend fun load(): T?

    suspend fun clear()
}