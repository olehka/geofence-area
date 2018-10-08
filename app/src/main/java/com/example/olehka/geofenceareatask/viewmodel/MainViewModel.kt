package com.example.olehka.geofenceareatask.viewmodel

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.olehka.geofenceareatask.geofence.GeofenceManager
import com.example.olehka.geofenceareatask.geofence.GeofenceTransitionsIntentService
import com.example.olehka.geofenceareatask.ui.TAG
import com.google.android.gms.location.Geofence

class MainViewModel(
        application: Application,
        private val geofenceManager: GeofenceManager
) : AndroidViewModel(application) {

    enum class Status {
        INSIDE, OUTSIDE
    }

    val networkLiveData = NetworkLiveData(application)
    val geofenceLiveData: LiveData<Int> = GeofenceTransitionsIntentService.geofenceData
    val mediatorLiveData = MediatorLiveData<Status>()

    var wifiName: String? = null

    init {
        mediatorLiveData.addSource(networkLiveData) { updateStatus() }
        mediatorLiveData.addSource(geofenceLiveData) { updateStatus() }
    }

    override fun onCleared() {
        if (hasGeofencePermissions()) {
            geofenceManager.removeGeofences()
        }
    }

    fun updateStatus() {
        if (checkWifiZone() || checkGeofenceZone()) {
            mediatorLiveData.value = Status.INSIDE
        } else {
            mediatorLiveData.value = Status.OUTSIDE
        }
    }

    fun startGeofencing(latitude: Double?, longitude: Double?, radius: Float?): Boolean {
        if (latitude == null || longitude == null || radius == null ||
                latitude < -90 || latitude > 90 ||
                longitude < -180 || longitude > 180 ||
                radius < 0) {
            Log.e(TAG, "Invalid geofencing data")
            return false
        }
        geofenceManager.createGeofenceObject(latitude, longitude, radius)
        if (hasGeofencePermissions()) {
            geofenceManager.removeGeofences()
            geofenceManager.addGeofences()
        }
        return true
    }

    fun addGeofences() = geofenceManager.addGeofences()

    private fun checkWifiZone(): Boolean {
        if (wifiName.isNullOrEmpty() || networkLiveData.value.isNullOrEmpty()) {
            return false
        }
        val wifiSsid = networkLiveData.value!!.replace("\"", "")
        return wifiName!!.toLowerCase() == wifiSsid.toLowerCase()
    }

    private fun checkGeofenceZone(): Boolean {
        if (geofenceLiveData.value == null) {
            return false
        }
        return geofenceLiveData.value == Geofence.GEOFENCE_TRANSITION_ENTER
    }

    private fun hasGeofencePermissions(): Boolean = ContextCompat.checkSelfPermission(getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
