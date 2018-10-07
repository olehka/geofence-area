package com.example.olehka.geofenceareatask

import android.app.IntentService
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitionsIntentService : IntentService(TAG) {

    companion object {
        val geofenceData = MutableLiveData<Int>()
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
            if (geofenceData.hasActiveObservers()) {
                geofenceData.postValue(geofenceTransition)
            }
        } else {
            Log.e(TAG, "Geofence transition: Invalid type")
        }
    }
}