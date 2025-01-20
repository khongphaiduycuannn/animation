package com.ndmq.android_mvvm_project_base.utils.extension

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


fun <T> Fragment.getArgumentsParcelable(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(key, clazz)
    } else {
        arguments?.getParcelable(key) as? T
    }
}


fun <T> Fragment.setNavigationResult(key: String, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}


fun <T> Fragment.observeNavigationResult(key: String, observer: (T) -> Unit) {
    val navController = findNavController()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    savedStateHandle?.getLiveData<T>(key)?.observe(viewLifecycleOwner) {
        observer(it)
    }
}

fun <T> Fragment.observeNavigationResultOnce(key: String, observer: (T) -> Unit) {
    val navController = findNavController()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    savedStateHandle?.getLiveData<T>(key)?.observe(viewLifecycleOwner) {
        if (it != null) {
            observer(it)
            savedStateHandle[key] = null
        }
    }
}


fun Fragment.navigate(
    actionId: Int,
    bundle: Bundle? = null,
    onError: ((Exception) -> Unit)? = null
) {
    try {
        findNavController().navigate(actionId, bundle)
    } catch (ex: Exception) {
        ex.printStackTrace()
        onError?.invoke(ex)
    }
}