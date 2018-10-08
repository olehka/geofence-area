package com.example.olehka.geofenceareatask.ui

import android.Manifest
import android.arch.lifecycle.Observer
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
import com.example.olehka.geofenceareatask.R
import com.example.olehka.geofenceareatask.databinding.MainFragmentBinding
import com.example.olehka.geofenceareatask.util.InjectorUtility
import com.example.olehka.geofenceareatask.viewmodel.MainViewModel

class MainFragment : Fragment() {

    companion object {
        const val REQUEST_CODE_GEOFENCE = 1001
        const val GEOFENCE_EXPIRATION_DURATION = 24L * 60 * 60 * 1000 // 24 hours
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.buttonCheck.setOnClickListener { onCheckClicked() }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = InjectorUtility.provideMainViewModelFactory(activity!!.application)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.mediatorLiveData.observe(this, Observer { updateStatus(it) })
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult: requestCode = $requestCode")
        when (requestCode) {
            REQUEST_CODE_GEOFENCE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.addGeofences()
                } else {
                    Log.e(TAG, "Permission denied")
                    Snackbar.make(binding.root, R.string.permission_denied_explanation, Snackbar.LENGTH_LONG)
                }
            }
        }
    }

    private fun onCheckClicked() {
        viewModel.wifiName = binding.wifiEdit.text.toString()
        val hasValidGeofences = viewModel.startGeofencing(
                binding.latitudeEdit.text.toString().toDoubleOrNull(),
                binding.longitudeEdit.text.toString().toDoubleOrNull(),
                binding.radiusEdit.text.toString().toFloatOrNull()
        )
        viewModel.updateStatus()
        if (hasValidGeofences && !hasGeofencePermissions()) {
            requestGeofencePermissions(REQUEST_CODE_GEOFENCE)
        }
    }

    private fun hasGeofencePermissions(): Boolean = ContextCompat.checkSelfPermission(context!!,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestGeofencePermissions(requestCode: Int) {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
    }

    private fun showInsideStatus() {
        binding.insideLabel.visibility = View.VISIBLE
        binding.outsideLabel.visibility = View.GONE
    }

    private fun showOutsideStatus() {
        binding.insideLabel.visibility = View.GONE
        binding.outsideLabel.visibility = View.VISIBLE
    }

    private fun updateStatus(status: MainViewModel.Status?) {
        if (status == MainViewModel.Status.INSIDE) {
            showInsideStatus()
        } else {
            showOutsideStatus()
        }
    }
}