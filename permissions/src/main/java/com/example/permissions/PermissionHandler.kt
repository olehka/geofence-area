package com.example.permissions

interface PermissionHandler {

    fun onPermissionResult(permissions: Array<String>, grantResults: IntArray)
}