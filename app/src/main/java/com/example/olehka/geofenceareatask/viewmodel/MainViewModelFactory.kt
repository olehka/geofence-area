package com.example.olehka.geofenceareatask.viewmodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.olehka.geofenceareatask.geofence.GeofenceManager

class MainViewModelFactory(
        private val application: Application,
        private val geofenceManager: GeofenceManager
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, geofenceManager) as T
    }
}