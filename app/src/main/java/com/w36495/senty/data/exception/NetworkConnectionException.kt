package com.w36495.senty.data.exception

import java.io.IOException

class NetworkConnectionException : IOException() {
    override val message: String
        get() = "네트워크 연결에 실패하였습니다."
}