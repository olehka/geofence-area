package com.example.permissions

interface PermissionManager {

    fun requestAndRun(permissions: List<String>, failAction: (List<String>) -> Unit, action: () -> Unit)

    fun requestThenRun(permissions: List<String>, failAction: () -> Unit, action: () -> Unit)
}