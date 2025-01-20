package com.ndmq.android_mvvm_project_base.base

sealed class DataState<out T> {

    data class Success<T>(val data: T) : DataState<T>()

    data class Error(val exception: Exception) : DataState<Nothing>()
}