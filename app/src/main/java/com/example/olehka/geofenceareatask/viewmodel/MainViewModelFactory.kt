package com.example.olehka.geofenceareatask.viewmodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.olehka.geofenceareatask.data.Repository

class MainViewModelFactory(
        private val application: Application,
        private val repository: Repository
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, repository) as T
    }
}