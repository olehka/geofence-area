package com.example.permissions

import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED

class UnnecessaryPermissionsHandler(
        private val failAction: (List<String>) -> Unit,
        private val action: () -> Unit) : PermissionHandler {

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        val denied = grantResults.indices.filter { grantResults[it] != PERMISSION_GRANTED }
        if (denied.isNotEmpty()) {
            failAction(denied.map { permissions[it] })
        }
        action()
    }
}