package com.example.olehka.geofenceareatask.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.olehka.geofenceareatask.ui.MainFragment
import com.example.olehka.geofenceareatask.ui.TAG
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceManager(val context: Context) {

    companion object {
        const val REQUEST_ID = "1001"
    }

    private val geofencingClient = LocationServices.getGeofencingClient(context)
    private lateinit var geofence: Geofence

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceTransitionsIntentService::class.java)
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun createGeofenceObject(latitude: Double, longitude: Double, radius: Float) {
        geofence = Geofence.Builder()
                .setRequestId(REQUEST_ID)
                .setCircularRegion(
                        latitude,
                        longitude,
                        radius
                )
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or
                                Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .setExpirationDuration(MainFragment.GEOFENCE_EXPIRATION_DURATION)
                .build()
    }

    fun getGeofenceRequest(): GeofencingRequest =
            GeofencingRequest.Builder().apply {
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or
                        GeofencingRequest.INITIAL_TRIGGER_EXIT)
                addGeofence(geofence)
            }.build()

    @SuppressLint("MissingPermission")
    fun addGeofences() {
        geofencingClient.addGeofences(getGeofenceRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                Log.i(TAG, "Geofence started successfully =]")
            }
            addOnFailureListener {
                Log.e(TAG, "Geofence started failed =[")
            }
        }
    }

    fun removeGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                Log.i(TAG, "Geofence stopped successfully =]")
            }
            addOnFailureListener {
                Log.e(TAG, "Geofence stopped failed =[")
            }
        }
    }
}