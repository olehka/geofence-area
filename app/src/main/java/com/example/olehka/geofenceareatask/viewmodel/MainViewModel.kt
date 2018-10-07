package com.example.olehka.geofenceareatask.viewmodel

import android.arch.lifecycle.ViewModel
import com.example.olehka.geofenceareatask.geofence.GeofenceManager

class MainViewModel(val geofenceManager: GeofenceManager) : ViewModel() {

    override fun onCleared() {
        geofenceManager.removeGeofences()
    }
}
