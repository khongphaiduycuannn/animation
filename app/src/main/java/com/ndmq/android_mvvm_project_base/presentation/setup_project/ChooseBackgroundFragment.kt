package com.ndmq.android_mvvm_project_base.presentation.setup_project

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.data.source.asset.BackgroundUtils
import com.ndmq.android_mvvm_project_base.databinding.FragmentChooseBackgroundBinding
import com.ndmq.android_mvvm_project_base.presentation.setup_project.adapter.BackgroundAdapter
import com.ndmq.android_mvvm_project_base.utils.extension.setNavigationResult

class ChooseBackgroundFragment : BaseFragment<FragmentChooseBackgroundBinding>(
    FragmentChooseBackgroundBinding::inflate
) {

    private val adapter = BackgroundAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()

        handleEvent()
    }

    private fun initView() {
        binding.rclBackgrounds.adapter = adapter

        val data = BackgroundUtils.getCategoryBackgroundList(requireContext())
        adapter.setItemsData(data)
    }

    private fun handleEvent() {
        adapter.onBackgroundClick = {
            setNavigationResult("BACKGROUND_PATH", it.path)
            findNavController().navigateUp()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}