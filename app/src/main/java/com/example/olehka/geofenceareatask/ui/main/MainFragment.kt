package com.example.olehka.geofenceareatask.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.olehka.geofenceareatask.GeofenceTransitionsIntentService
import com.example.olehka.geofenceareatask.R
import com.example.olehka.geofenceareatask.TAG
import com.example.olehka.geofenceareatask.databinding.MainFragmentBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class MainFragment : Fragment() {

    companion object {
        const val PERMISSION_ACCESS_FINE_LOCATION = 1
        const val GEOFENCE_EXPIRATION_DURATION = 24L * 60 * 60 * 1000 // 24 hours
        fun newInstance() = MainFragment()
    }

    enum class PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofence: Geofence
    private lateinit var binding: MainFragmentBinding
    private var pendingGeofenceTask = PendingGeofenceTask.NONE
    private var started = false

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceTransitionsIntentService::class.java)
        PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.button.setOnClickListener {
            processButtonClick()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        geofencingClient = LocationServices.getGeofencingClient(context!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult: requestCode = $requestCode")
        when (requestCode) {
            PERMISSION_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when (pendingGeofenceTask) {
                        PendingGeofenceTask.ADD -> addGeofences()
                        PendingGeofenceTask.REMOVE -> removeGeofences()
                        else -> {}
                    }
                } else {
                    Log.e(TAG, "Permission denied")
                    Snackbar.make(binding.root, R.string.permission_denied_explanation, Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    fun processButtonClick() {
        if (started) {
            stopGeofencing()
        } else {
            startGeofencing()
        }
    }

    fun startGeofencing() {
        val latitude = binding.latitudeEdit.text.toString().toDoubleOrNull()
        val longitude = binding.longitudeEdit.text.toString().toDoubleOrNull()
        val radius = binding.radiusEdit.text.toString().toFloatOrNull()
        if (latitude == null || longitude == null || radius == null) {
            Log.e(TAG, "Parsing error")
            return
        }
        createGeofenceObject(latitude, longitude, radius)
        addGeofencing()
    }

    fun createGeofenceObject(latitude: Double, longitude: Double, radius: Float) {
        geofence = Geofence.Builder()
                .setRequestId("")
                .setCircularRegion(
                        latitude,
                        longitude,
                        radius
                )
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or
                                Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_DURATION)
                .build()
    }

    fun getGeofenceRequest(): GeofencingRequest =
            GeofencingRequest.Builder().apply {
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                addGeofence(geofence)
            }.build()

    fun addGeofencing() {
        pendingGeofenceTask = PendingGeofenceTask.ADD
        if (hasGeofencePermissions()) {
            addGeofences()
        } else {
            requestGeofencePermissions()
        }
    }

    fun stopGeofencing() {
        pendingGeofenceTask = PendingGeofenceTask.REMOVE
        if (hasGeofencePermissions()) {
            removeGeofences()
        } else {
            requestGeofencePermissions()
        }

    }

    fun hasGeofencePermissions(): Boolean =
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

    fun requestGeofencePermissions() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    fun addGeofences() {
        geofencingClient.addGeofences(getGeofenceRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                Log.i(TAG, "Geofence started successfully =]")
                binding.button.text = getString(R.string.stop)
                started = true
            }
            addOnFailureListener {
                Log.e(TAG, "Geofence started failed =[")
            }
        }
    }

    fun removeGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.i(TAG, "Geofence stopped successfully =]")
                binding.button.text = getString(R.string.start)
                started = false
            }
            addOnFailureListener {
                Log.e(TAG, "Geofence stopped failed =[")
            }
        }
    }
}