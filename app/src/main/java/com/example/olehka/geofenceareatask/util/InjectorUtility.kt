package com.example.olehka.geofenceareatask.util

import android.app.Application
import android.content.Context
import com.example.olehka.geofenceareatask.data.Repository
import com.example.olehka.geofenceareatask.geofence.GeofenceManager
import com.example.olehka.geofenceareatask.geofence.GeofenceTransitionsIntentService
import com.example.olehka.geofenceareatask.viewmodel.MainViewModelFactory
import com.example.olehka.geofenceareatask.viewmodel.NetworkLiveData

object InjectorUtility {

    fun provideMainViewModelFactory(application: Application): MainViewModelFactory =
            MainViewModelFactory(application, getRepository(application))

    private fun getGeofenceManager(context: Context): GeofenceManager =
            GeofenceManager(context)

    private fun getRepository(context: Context) =
            Repository.getInstance(
                    getGeofenceManager(context),
                    NetworkLiveData(context),
                    GeofenceTransitionsIntentService.geofenceData
            )
}