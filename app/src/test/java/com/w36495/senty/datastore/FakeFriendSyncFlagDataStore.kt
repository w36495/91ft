package com.w36495.senty.datastore

import com.w36495.senty.domain.local.datastore.DataStoreContact

class FakeFriendSyncFlagDataStore : DataStoreContact<Boolean> {
    private var flag: Boolean? = null

    override suspend fun save(value: Boolean) {
        flag = value
    }

    override suspend fun load(): Boolean? {
        return flag
    }

    override suspend fun clear() {
        flag = null
    }
}