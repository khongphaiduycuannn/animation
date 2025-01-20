package com.ndmq.android_mvvm_project_base.presentation.create_project

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson.GsonConfig
import com.ndmq.android_mvvm_project_base.data.model.Frame
import com.ndmq.android_mvvm_project_base.databinding.FragmentPreviewBinding
import com.ndmq.android_mvvm_project_base.utils.extension.observeNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreviewFragment : BaseFragment<FragmentPreviewBinding>(
    FragmentPreviewBinding::inflate
) {

    private val viewModel by viewModels<PreviewViewModel>()


    private val frameBitmaps = mutableListOf<Bitmap>()

    private var delayTime = 1000L / 7

    private var timer: Flow<Long>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()

        observeData()
    }


    private fun initData() {
        arguments?.let {
            val projectId = it.getInt("PROJECT_ID")
            viewModel.getProject(projectId.toLong())
        }
    }


    private fun observeData() {
        viewModel.project.observeNotNull(viewLifecycleOwner) {
            val backgroundColor = it.backgroundColor
            val backgroundPath = it.backgroundPath
            val widthHeightRatio = it.widthHeightRatio
            val frames = GsonConfig
                .getConfigurationGson()
                .fromJson(it.frames, Array<Frame>::class.java)
                .toList()

            val heightWidthRatio = 1f / widthHeightRatio
            binding.ivColorBackground.heightWidthRatio = heightWidthRatio
            binding.ivImageBackground.heightWidthRatio = heightWidthRatio
            binding.drawView.heightWidthRatio = heightWidthRatio

            if (backgroundColor != null) {
                binding.ivColorBackground.visibility = View.VISIBLE
                binding.ivImageBackground.visibility = View.INVISIBLE
                binding.ivColorBackground.setBackgroundColor(backgroundColor)
            }

            if (backgroundPath != null) {
                binding.ivColorBackground.visibility = View.INVISIBLE
                binding.ivImageBackground.visibility = View.VISIBLE
                Glide.with(requireContext()).load(backgroundPath).centerCrop()
                    .into(binding.ivImageBackground)
            }

            delayTime = 1000L / it.fps

            recycleFrameBitmaps()
            frameBitmaps.clear()
            frames.forEach { frame ->
                binding.drawView.frame = frame
                val bitmap = binding.drawView.getDrawBitmap()
                bitmap?.let {
                    frameBitmaps.add(bitmap.copy(Bitmap.Config.ARGB_8888, true))
                }
            }

            timer = (1..Long.MAX_VALUE)
                .asSequence()
                .asFlow()
                .onEach { delay(delayTime) }

            lifecycleScope.launch {
                var index = 0
                timer?.collect {
                    if (frameBitmaps.isNotEmpty() && index < frameBitmaps.size - 1) {
                        Glide.with(requireContext()).load(frameBitmaps[index])
                            .into(binding.previewView)
                    }
                    index = if (index >= frameBitmaps.size - 1) 0 else index + 1
                }
            }
        }
    }

    private fun recycleFrameBitmaps() {
        frameBitmaps.forEach {
            it.recycle()
        }
    }
}