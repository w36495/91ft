package com.w36495.senty.data.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.w36495.senty.data.exception.NetworkConnectionException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(
    private val context: Context,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (!isNetworkConnection()) {
            throw NetworkConnectionException()
        } else {
            chain.proceed(request)
        }
    }

    private fun isNetworkConnection(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)

        val hasWifi = connection?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellular = connection?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        return connection != null && (hasWifi == true || hasCellular == true)
    }
}