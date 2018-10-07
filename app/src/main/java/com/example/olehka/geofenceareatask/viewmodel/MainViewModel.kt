package com.example.olehka.geofenceareatask.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.olehka.geofenceareatask.geofence.GeofenceManager

class MainViewModel(
        application: Application,
        val geofenceManager: GeofenceManager
) : AndroidViewModel(application) {

    val networkLiveData = NetworkLiveData(application)

    override fun onCleared() {
        geofenceManager.removeGeofences()
    }
}
