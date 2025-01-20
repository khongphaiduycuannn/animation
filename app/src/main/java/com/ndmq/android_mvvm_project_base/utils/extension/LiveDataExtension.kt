package com.ndmq.android_mvvm_project_base.utils.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData


fun <T> MutableLiveData<T>.observeNotNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner) {
        it?.let(observer)
    }
}