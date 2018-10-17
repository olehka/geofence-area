package com.example.permissions

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat


class PermissionManagerImpl : FragmentLifecycleObserver, ActivityLifecycleObserver, PermissionManager {

    private val requestedPermissionHandlers = HashMap<Int, PermissionHandler>()
    private val pendingResults = HashMap<Int, PermissionResult>()

    lateinit var permissionRequester: (Array<String>, Int) -> Unit
    lateinit var permissionChecker: (String) -> Boolean

    override fun initWith(owner: Fragment) {
        permissionRequester = { permissions, id -> owner.requestPermissions(permissions, id) }
        permissionChecker = { ContextCompat.checkSelfPermission(owner.context!!, it) == PERMISSION_GRANTED }
    }

    override fun initWith(owner: Activity) {
        permissionRequester = { permissions, id -> ActivityCompat.requestPermissions(owner, permissions, id) }
        permissionChecker = { ContextCompat.checkSelfPermission(owner, it) == PERMISSION_GRANTED }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun ready() {
        pendingResults.forEach { it.value.onPermissionResult() }
        pendingResults.clear()
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestedPermissionHandlers.containsKey(requestCode)) {
            pendingResults[requestCode] = PermissionResult(requestedPermissionHandlers.remove(requestCode)!!, permissions, grantResults)
        }
    }

    override fun requestAndRun(permissions: List<String>, failAction: (List<String>) -> Unit, action: () -> Unit) {
        request(permissions, UnnecessaryPermissionsHandler(failAction, action), action)
    }

    override fun requestThenRun(permissions: List<String>, failAction: () -> Unit, action: () -> Unit) {
        request(permissions, RequiredPermissionsHandler(failAction, action), action)
    }

    private fun request(permissions: List<String>, handler: PermissionHandler, action: () -> Unit) {
        val notGrantedPermissions = ArrayList(permissions.filterNot { permissionChecker(it) })
        val permissionArray = notGrantedPermissions.toTypedArray()

        if (permissionArray.isEmpty()) {
            action()
        } else {
            val id = Math.abs(handler.hashCode().toShort().toInt())
            requestedPermissionHandlers[id] = handler //todo invent another way to generate keys
            permissionRequester(permissionArray, id)
        }
    }

    private class PermissionResult internal constructor(
            private val permissionHandler: PermissionHandler,
            private val resultPermissions: Array<String>,
            private val grantResults: IntArray) {

        fun onPermissionResult() = permissionHandler.onPermissionResult(resultPermissions, grantResults)
    }
}