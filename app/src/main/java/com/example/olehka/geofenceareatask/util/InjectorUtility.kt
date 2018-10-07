package com.example.olehka.geofenceareatask.util

import android.app.Application
import android.content.Context
import com.example.olehka.geofenceareatask.geofence.GeofenceManager
import com.example.olehka.geofenceareatask.viewmodel.MainViewModelFactory

object InjectorUtility {

    fun provideMainViewModelFactory(application: Application): MainViewModelFactory =
            MainViewModelFactory(application, getGeofenceManager(application))

    private fun getGeofenceManager(context: Context): GeofenceManager = GeofenceManager(context)
}