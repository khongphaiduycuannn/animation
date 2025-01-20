package com.ndmq.android_mvvm_project_base.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    val message = MutableLiveData<String>()

    val error = MutableLiveData<Exception>()

    val isLoading = MutableLiveData(false)
}