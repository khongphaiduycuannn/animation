package com.ndmq.android_mvvm_project_base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint


open class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB
) : Fragment() {

    protected val binding: VB by lazy {
        bindingInflater(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    protected fun setOnBackPressed(doOnBackPressed: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    doOnBackPressed()
                }
            }
        )
    }
}