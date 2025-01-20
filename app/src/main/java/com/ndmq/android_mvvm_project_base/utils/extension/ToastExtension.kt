package com.ndmq.android_mvvm_project_base.utils.extension

import android.content.Context
import android.widget.Toast

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun Context.showToast(stringResID: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, getString(stringResID), duration).show()
}