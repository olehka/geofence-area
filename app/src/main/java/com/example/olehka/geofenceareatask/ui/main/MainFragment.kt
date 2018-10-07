package com.example.olehka.geofenceareatask.ui.main

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.olehka.geofenceareatask.GeofenceManager
import com.example.olehka.geofenceareatask.R
import com.example.olehka.geofenceareatask.TAG
import com.example.olehka.geofenceareatask.databinding.MainFragmentBinding
import com.example.olehka.geofenceareatask.util.InjectorUtility

class MainFragment : Fragment() {

    companion object {
        const val GEOFENCE_EXPIRATION_DURATION = 24L * 60 * 60 * 1000 // 24 hours
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.buttonStart.setOnClickListener { startGeofencing() }
        binding.buttonStop.setOnClickListener { stopGeofencing() }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = InjectorUtility.provideMainViewModelFactory(context!!.applicationContext)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult: requestCode = $requestCode")
        when (requestCode) {
            GeofenceManager.ADD_GEOFENCE, GeofenceManager.REMOVE_GEOFENCE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.geofenceManager.let {
                        if (requestCode == GeofenceManager.ADD_GEOFENCE) it.addGeofences()
                        else it.removeGeofences()
                    }
                } else {
                    Log.e(TAG, "Permission denied")
                    Snackbar.make(binding.root, R.string.permission_denied_explanation, Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    private fun startGeofencing() {
        val latitude = binding.latitudeEdit.text.toString().toDoubleOrNull()
        val longitude = binding.longitudeEdit.text.toString().toDoubleOrNull()
        val radius = binding.radiusEdit.text.toString().toFloatOrNull()
        if (latitude == null || longitude == null || radius == null) {
            Log.e(TAG, "Parsing error")
            return
        }
        viewModel.geofenceManager.createGeofenceObject(latitude, longitude, radius)
        if (hasGeofencePermissions()) {
            viewModel.geofenceManager.addGeofences()
        } else {
            requestGeofencePermissions(GeofenceManager.ADD_GEOFENCE)
        }
    }

    private fun stopGeofencing() {
        if (hasGeofencePermissions()) {
            viewModel.geofenceManager.removeGeofences()
        } else {
            requestGeofencePermissions(GeofenceManager.REMOVE_GEOFENCE)
        }

    }

    private fun hasGeofencePermissions(): Boolean = ContextCompat.checkSelfPermission(context!!,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestGeofencePermissions(requestCode: Int) {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
    }
}