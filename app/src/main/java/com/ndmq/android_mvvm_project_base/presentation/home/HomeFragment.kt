package com.ndmq.android_mvvm_project_base.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.databinding.FragmentHomeBinding
import com.ndmq.android_mvvm_project_base.utils.extension.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {

    private val viewModel by viewModels<HomeViewModel>()

    private val projectAdapter by lazy { ProjectAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()

        handleEvent()

        observeData()
    }

    private fun initView() {
        binding.rclProjects.adapter = projectAdapter
    }

    private fun handleEvent() {
        binding.btnCreateProject.setOnClickListener {
            navigate(R.id.action_homeFragment_to_setupProjectFragment)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.allProjects.collect {
                projectAdapter.setData(it)
            }
        }
    }
}