package com.example.olehka.geofenceareatask.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.util.Log
import com.example.olehka.geofenceareatask.ui.TAG

class NetworkLiveData(val application: Application) : LiveData<Int>() {

    private val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val wifiManager =
            application.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            val ssid = wifiManager.connectionInfo.ssid
            Log.i(TAG, "Network available: $ssid")
        }

        override fun onLost(network: Network?) {
            Log.i(TAG, "Network lost")
        }
    }

    override fun onActive() {
        super.onActive()
        val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(callback)
    }
}