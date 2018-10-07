package com.example.olehka.geofenceareatask.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.olehka.geofenceareatask.geofence.GeofenceManager

class MainViewModelFactory(private val geofenceManager: GeofenceManager)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(geofenceManager) as T
    }
}