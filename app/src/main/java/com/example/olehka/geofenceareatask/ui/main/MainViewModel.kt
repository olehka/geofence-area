package com.example.olehka.geofenceareatask.ui.main

import android.arch.lifecycle.ViewModel
import com.example.olehka.geofenceareatask.GeofenceManager

class MainViewModel(val geofenceManager: GeofenceManager) : ViewModel() {

    override fun onCleared() {
        geofenceManager.removeGeofences()
    }
}
