package com.example.olehka.geofenceareatask.geofence

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.example.olehka.geofenceareatask.data.Repository
import com.example.olehka.geofenceareatask.ui.TAG
import com.example.olehka.geofenceareatask.util.InjectorUtility
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitionsIntentService : IntentService(TAG) {

    private lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        repository = InjectorUtility.getRepository(applicationContext)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "onHandleIntent")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e(TAG, geofencingEvent.errorCode.toString())
            return
        }
        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "Geofence ENTER/EXIT transition: $geofenceTransition")
            repository.setGeofenceLiveData(geofenceTransition)
        } else {
            Log.e(TAG, "Geofence transition: Invalid type")
        }
    }
}