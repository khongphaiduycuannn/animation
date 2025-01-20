package com.ndmq.android_mvvm_project_base.utils.extension

import android.content.Context

fun Context.getStatusBarHeight(): Int {
    var result = 25
    try {
        val resourceId: Int =
            resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

fun Context.getNavigationBarHeight(): Int {
    var result = 25
    try {
        val resourceId: Int =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

