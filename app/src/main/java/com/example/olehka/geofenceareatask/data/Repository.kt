package com.example.olehka.geofenceareatask.data

import android.arch.lifecycle.LiveData
import com.example.olehka.geofenceareatask.geofence.GeofenceManager
import com.example.olehka.geofenceareatask.viewmodel.NetworkLiveData

class Repository(
        private val geofenceManager: GeofenceManager,
        val networkLiveData: NetworkLiveData,
        val geofenceLiveData: LiveData<Int>
) {

    companion object {

        @Volatile
        private var instance: Repository? = null

        fun getInstance(
                geofenceManager: GeofenceManager,
                networkLiveData: NetworkLiveData,
                geofenceLiveData: LiveData<Int>
        ) =
                instance ?: synchronized(this) {
                    instance ?: Repository(geofenceManager, networkLiveData, geofenceLiveData)
                            .also { instance = it }
                }
    }

    fun removeGeofences() = geofenceManager.removeGeofences()

    fun restartGeofences() {
        geofenceManager.removeGeofences()
        geofenceManager.addGeofences()
    }

    fun createGeofenceObject(latitude: Double, longitude: Double, radius: Float) =
            geofenceManager.createGeofenceObject(latitude, longitude, radius)
}