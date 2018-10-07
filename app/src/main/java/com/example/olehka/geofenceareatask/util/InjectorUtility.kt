package com.example.olehka.geofenceareatask.util

import android.content.Context
import com.example.olehka.geofenceareatask.geofence.GeofenceManager
import com.example.olehka.geofenceareatask.viewmodel.MainViewModelFactory

object InjectorUtility {

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory =
            MainViewModelFactory(getGeofenceManager(context))

    private fun getGeofenceManager(context: Context): GeofenceManager =
            GeofenceManager(context)
}