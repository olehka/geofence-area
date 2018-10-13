package com.example.olehka.geofenceareatask.data

import android.arch.lifecycle.MutableLiveData
import com.example.olehka.geofenceareatask.geofence.GeofenceManager
import com.example.olehka.geofenceareatask.viewmodel.NetworkLiveData

class Repository(
        private val geofenceManager: GeofenceManager,
        private val networkLiveData: NetworkLiveData
) {

    companion object {

        @Volatile
        private var instance: Repository? = null

        fun getInstance(
                geofenceManager: GeofenceManager,
                networkLiveData: NetworkLiveData
        ) =
                instance ?: synchronized(this) {
                    instance ?: Repository(geofenceManager, networkLiveData)
                            .also { instance = it }
                }
    }

    private val geofenceLiveData: MutableLiveData<Int> = MutableLiveData()

    fun removeGeofences() = geofenceManager.removeGeofences()

    fun restartGeofences() {
        geofenceManager.removeGeofences()
        geofenceManager.addGeofences()
    }

    fun createGeofenceObject(latitude: Double, longitude: Double, radius: Float) =
            geofenceManager.createGeofenceObject(latitude, longitude, radius)

    fun getNetworkLiveData() = networkLiveData

    fun getGeofenceLiveData() = geofenceLiveData

    fun setGeofenceLiveData(value: Int) = geofenceLiveData.postValue(value)
}