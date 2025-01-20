package com.ndmq.android_mvvm_project_base.presentation.setup_project

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import com.ndmq.android_mvvm_project_base.databinding.FragmentChooseFormatBinding
import com.ndmq.android_mvvm_project_base.utils.extension.setNavigationResult

class ChooseFormatFragment : BaseFragment<FragmentChooseFormatBinding>(
    FragmentChooseFormatBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        handleEvent()
    }

    private fun initView() {
        arguments?.getString("CURRENT_FORMAT")?.let {
            when (it) {
                DrawProject.MP4 -> binding.rbMp4Format.isChecked = true
                DrawProject.GIF -> binding.rbGifFormat.isChecked = true
            }
        }
    }

    private fun handleEvent() {
        binding.rbMp4Format.setOnClickListener {
            binding.rbMp4Format.isChecked = true
            binding.rbGifFormat.isChecked = false
            setNavigationResult("OUTPUT_FORMAT", DrawProject.MP4)
        }

        binding.llGifFormat.setOnClickListener {
            binding.rbMp4Format.isChecked = false
            binding.rbGifFormat.isChecked = true
            setNavigationResult("OUTPUT_FORMAT", DrawProject.GIF)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}