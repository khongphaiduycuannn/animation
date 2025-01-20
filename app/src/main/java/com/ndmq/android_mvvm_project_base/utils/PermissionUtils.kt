package com.ndmq.android_mvvm_project_base.utils

import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


fun AppCompatActivity.isPermissionGranted(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) == 0
}

fun AppCompatActivity.shouldShowPermissionRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

fun AppCompatActivity.openSettingPermission() {
    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}


class PermissionUtils(
    private val activity: AppCompatActivity
) {

    interface IOpenSettingCallback {
        fun invoke()
    }

    interface PermissionCallback {
        fun onAllGranted(permission: List<String>)
        fun onDenied(listGranted: List<String>, listDenied: List<String>)
        fun onShouldOpenSetting(callback: IOpenSettingCallback)
    }

    private var permissions: List<String> = listOf()

    private val isPermissionGranted: Boolean
        get() = permissions.all { activity.isPermissionGranted(it) }


    private val isShouldShowPermissionRationale: Boolean
        get() = permissions.any { activity.shouldShowPermissionRationale(it) }


    fun requestPermission(
        permissions: List<String>,
        callback: PermissionCallback,
        doOnPreRequest: () -> Unit = {}
    ) {
        doOnPreRequest()

        this.permissions = permissions

        if (isGrantAll()) {
            callback.onAllGranted(permissions)
            return
        }

        val contract = ActivityResultContracts.RequestMultiplePermissions()
        activity.activityResultRegistry
            .register("requestPermission", contract) { newResult ->
                if (isGrantAll()) {
                    callback.onAllGranted(permissions)
                    return@register
                }

                val granted = newResult.filter { it.value }.keys.toList()
                val denied = newResult.filter { !it.value }.keys.toList()

                callback.onDenied(granted, denied)

                if (!isShouldShowPermissionRationale) {
                    callback.onShouldOpenSetting(object : IOpenSettingCallback {
                        override fun invoke() {
                            activity.openSettingPermission()
                        }
                    })
                }

            }.launch(permissions.toTypedArray())
    }


    private fun isGrantAll(): Boolean {
        return isPermissionGranted
    }
}