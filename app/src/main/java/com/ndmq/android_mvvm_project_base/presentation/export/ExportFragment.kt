package com.ndmq.android_mvvm_project_base.presentation.export

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.databinding.FragmentExportBinding
import com.ndmq.android_mvvm_project_base.presentation.create_project.DrawViewModel
import com.ndmq.android_mvvm_project_base.utils.extension.navigate

class ExportFragment : BaseFragment<FragmentExportBinding>(
    FragmentExportBinding::inflate
) {

    private val viewModel by viewModels<DrawViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleEvent()
    }

    private fun handleEvent() {
        binding.btnHome.setOnClickListener {
            navigate(R.id.action_exportFragment_to_homeFragment)
        }
    }
}