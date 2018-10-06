package com.example.olehka.geofenceareatask

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitionsIntentService : IntentService(TAG) {

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
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            Log.i(TAG, "Geofence transition: ENTER or EXIT")
        } else {
            Log.e(TAG, "Geofence transition: Invalid type")
        }
    }
}