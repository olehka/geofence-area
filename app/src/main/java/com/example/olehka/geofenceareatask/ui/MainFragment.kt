package com.example.olehka.geofenceareatask.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
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
import com.example.olehka.geofenceareatask.R
import com.example.olehka.geofenceareatask.databinding.MainFragmentBinding
import com.example.olehka.geofenceareatask.util.InjectorUtility
import com.example.olehka.geofenceareatask.viewmodel.MainViewModel
import com.example.permissions.PermissionManagerImpl
import com.example.permissions.addObserver
import com.google.android.gms.location.places.ui.PlacePicker

class MainFragment : Fragment() {

    companion object {
        const val REQUEST_CODE_GEOFENCE = 1001
        const val REQUEST_PLACE_PICKER = 1002
        const val GEOFENCE_EXPIRATION_DURATION = 24L * 60 * 60 * 1000 // 24 hours
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private val permissionManager = PermissionManagerImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.buttonCheck.setOnClickListener { checkStatus() }
        binding.buttonPlacePicker.setOnClickListener { launchPlacePicker() }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = InjectorUtility.provideMainViewModelFactory(activity!!.application)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
        viewModel.mediatorLiveData.observe(this, Observer { updateStatus(it) })
        lifecycle.addObserver(permissionManager, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult: requestCode = $requestCode")
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PLACE_PICKER -> {
                if (resultCode == RESULT_OK) {
                    val place = PlacePicker.getPlace(context, data)
                    binding.latitudeEdit.setText(place.latLng.latitude.toString())
                    binding.longitudeEdit.setText(place.latLng.longitude.toString())
                }
            }
        }
    }

    private fun launchPlacePicker() {
        val builder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(activity), REQUEST_PLACE_PICKER)
    }

    private fun checkStatus() {
        viewModel.wifiName = binding.wifiEdit.text.toString()
        viewModel.latitude = binding.latitudeEdit.text.toString().toDoubleOrNull()
        viewModel.longitude = binding.longitudeEdit.text.toString().toDoubleOrNull()
        viewModel.radius = binding.radiusEdit.text.toString().toFloatOrNull()
        viewModel.updateStatus()
        permissionManager.requestThenRun(
                listOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this::showPermissionDeniedError,
                viewModel::startGeofencing
        )
    }

    private fun showPermissionDeniedError() {
        Log.e(TAG, "Permission denied")
        Snackbar.make(binding.root, R.string.permission_denied_explanation, Snackbar.LENGTH_LONG)
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