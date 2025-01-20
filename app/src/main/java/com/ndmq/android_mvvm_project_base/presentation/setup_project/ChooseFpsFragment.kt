package com.ndmq.android_mvvm_project_base.presentation.setup_project

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.databinding.FragmentChooseFpsBinding
import com.ndmq.android_mvvm_project_base.presentation.setup_project.adapter.FpsAdapter
import com.ndmq.android_mvvm_project_base.utils.extension.setNavigationResult
import kotlin.math.abs
import kotlin.math.min


class ChooseFpsFragment : BaseFragment<FragmentChooseFpsBinding>(
    FragmentChooseFpsBinding::inflate
) {

    private val fpsAdapter = FpsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()

        handleEvent()
    }


    private fun initView() {
        binding.rclFps.adapter = fpsAdapter
        fpsAdapter.items = (1..24).toMutableList()

        arguments?.getInt("CURRENT_FPS")?.let {
            binding.rclFps.scrollToPosition(fpsAdapter.items.indexOf(it))
        }
    }

    private fun handleEvent() {
        fpsAdapter.onItemClick = { _, index ->
            val layoutManger = binding.rclFps.layoutManager as LinearLayoutManager
            val itemHeight = binding.rclFps.getChildAt(0).height
            val currentPosition = layoutManger.findFirstVisibleItemPosition()
            binding.rclFps.smoothScrollBy(0, itemHeight * (index - currentPosition))
        }

        binding.rclFps.addOnScrollListener(getOnScrollToTopListener())

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getOnScrollToTopListener(): OnScrollListener {
        return object : OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager?

                    if (layoutManager != null) {
                        var firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                        val firstVisibleView =
                            layoutManager.findViewByPosition(firstVisiblePosition)

                        if (firstVisibleView != null) {
                            val viewStart = firstVisibleView.top
                            val viewEnd = firstVisibleView.bottom
                            val itemHeight = firstVisibleView.height

                            var offSet = if (itemHeight / 2 < abs(viewStart)) {
                                firstVisiblePosition =
                                    min(firstVisiblePosition + 1, fpsAdapter.items.size - 5)
                                viewEnd
                            } else {
                                viewStart
                            }
                            if (viewEnd <= 5 || viewStart >= -5) {
                                offSet = 0
                            }

                            recyclerView.smoothScrollBy(0, offSet)
                            setNavigationResult("FPS", fpsAdapter.items[firstVisiblePosition])
                        }
                    }
                }
            }
        }
    }
}