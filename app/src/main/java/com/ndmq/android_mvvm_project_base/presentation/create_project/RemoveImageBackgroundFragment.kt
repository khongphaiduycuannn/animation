package com.ndmq.android_mvvm_project_base.presentation.create_project

import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.databinding.FragmentRemoveImageBackgroundBinding
import com.ndmq.android_mvvm_project_base.utils.ResizeImageUtils
import com.ndmq.android_mvvm_project_base.utils.extension.getArgumentsParcelable
import com.ndmq.android_mvvm_project_base.utils.extension.setNavigationResult
import com.ndmq.android_mvvm_project_base.utils.extension.showLoadingDialog
import com.ndmq.android_mvvm_project_base.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class RemoveImageBackgroundFragment : BaseFragment<FragmentRemoveImageBackgroundBinding>(
    FragmentRemoveImageBackgroundBinding::inflate
) {

    private val viewModel by viewModels<RemoveBackgroundViewModel>()


    private val dialog by lazy { Dialog(requireContext()) }


    private val selectedImagePath by lazy {
        getArgumentsParcelable("SELECTED_IMAGE_PATH", Uri::class.java)
    }


    private var foregroundBitmap: Bitmap? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()

        initView()

        handleEvent()

        observeData()
    }

    override fun onDestroy() {
        super.onDestroy()
        foregroundBitmap?.recycle()
    }


    private fun initData() {
        viewModel.projectFolderName = arguments?.getString("PROJECT_FOLDER_NAME") ?: ""
    }


    private fun initView() {
        Glide.with(requireContext()).load(selectedImagePath).into(binding.ivImage)
    }


    private fun handleEvent() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRemoveBackground.setOnClickListener {
            removeBackground()
        }

        binding.btnDone.setOnClickListener {
            selectedImagePath?.let {
                val bitmap =
                    foregroundBitmap ?: ResizeImageUtils(requireContext()).getResizedImageBitmap(it)

                viewModel.saveImage(
                    bitmap = bitmap!!,
                    onSaved = { path ->
                        setNavigationResult("REMOVED_BACKGROUND_IMAGE_PATH", path)
                        findNavController().navigateUp()
                    }
                )
            }
        }
    }


    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) dialog.showLoadingDialog() else dialog.dismiss()
        }
    }


    private fun removeBackground() {
        selectedImagePath?.let {
            viewModel.isLoading.value = true
            try {
                val bitmap = ResizeImageUtils(requireContext()).getResizedImageBitmap(it)
                val image: InputImage = InputImage.fromBitmap(bitmap!!, 0)
                val options = SubjectSegmenterOptions.Builder()
                    .enableForegroundBitmap()
                    .build()
                val segmenter = SubjectSegmentation.getClient(options)

                segmenter.process(image)
                    .addOnSuccessListener { result ->
                        foregroundBitmap = result.foregroundBitmap
                        Glide.with(requireContext()).load(foregroundBitmap).into(binding.ivImage)

                        viewModel.isLoading.value = false

                        context?.showToast("Success")
                    }
                    .addOnFailureListener { e ->
                        viewModel.isLoading.value = false
                        e.printStackTrace()

                        context?.showToast("Failure")
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                viewModel.isLoading.value = false

                context?.showToast("Failure")
            }
        }
    }
}