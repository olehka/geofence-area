package com.example.olehka.geofenceareatask.viewmodel

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.example.olehka.geofenceareatask.data.Repository
import com.google.android.gms.location.Geofence

class MainViewModel(
        application: Application,
        private val repository: Repository
) : AndroidViewModel(application) {

    enum class Status {
        INSIDE, OUTSIDE
    }

    val mediatorLiveData = MediatorLiveData<Status>()

    var wifiName: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var radius: Float? = null

    init {
        mediatorLiveData.addSource(repository.networkLiveData) { updateStatus() }
        mediatorLiveData.addSource(repository.geofenceLiveData) { updateStatus() }
    }

    override fun onCleared() {
        if (hasGeofencePermissions()) {
            repository.removeGeofences()
        }
    }

    fun updateStatus() {
        if (checkWifiZone() || checkGeofenceZone()) {
            mediatorLiveData.value = Status.INSIDE
        } else {
            mediatorLiveData.value = Status.OUTSIDE
        }
    }

    fun startGeofencing() {
        if (validGeofence()) {
            repository.createGeofenceObject(latitude!!, longitude!!, radius!!)
            repository.restartGeofences()
        }
    }

    private fun checkWifiZone(): Boolean {
        if (wifiName.isNullOrEmpty() || repository.networkLiveData.value.isNullOrEmpty()) {
            return false
        }
        val wifiSsid = repository.networkLiveData.value!!.replace("\"", "")
        return wifiName!!.toLowerCase() == wifiSsid.toLowerCase()
    }

    private fun checkGeofenceZone(): Boolean {
        if (!validGeofence() || repository.geofenceLiveData.value == null) {
            return false
        }
        return repository.geofenceLiveData.value == Geofence.GEOFENCE_TRANSITION_ENTER
    }

    private fun hasGeofencePermissions(): Boolean = ContextCompat.checkSelfPermission(getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun validGeofence(): Boolean =
            latitude != null && longitude != null && radius != null &&
                    latitude!! >= -90 && latitude!! <= 90 &&
                    longitude!! >= -180 && longitude!! <= 180 &&
                    radius!! > 0
}
