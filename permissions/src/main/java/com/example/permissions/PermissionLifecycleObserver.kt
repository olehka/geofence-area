package com.example.permissions

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.support.v4.app.Fragment

interface FragmentLifecycleObserver : LifecycleObserver {
    fun initWith(owner: Fragment)
}

interface ActivityLifecycleObserver : LifecycleObserver {
    fun initWith(owner: Activity)
}

fun Lifecycle.addObserver(observer: FragmentLifecycleObserver, fragment: Fragment) {
    addObserver(observer)
    observer.initWith(fragment)
}

fun Lifecycle.addObserver(observer: ActivityLifecycleObserver, activity: Activity) {
    addObserver(observer)
    observer.initWith(activity)
}