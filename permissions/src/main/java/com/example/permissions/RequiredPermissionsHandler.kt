package com.example.permissions

import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED

class RequiredPermissionsHandler(
        private val failAction: () -> Unit,
        private val action: () -> Unit) : PermissionHandler {

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        val denied = grantResults.indices.filter { grantResults[it] != PERMISSION_GRANTED }
        if (denied.isEmpty()) {
            action()
        } else {
            failAction()
        }
    }
}