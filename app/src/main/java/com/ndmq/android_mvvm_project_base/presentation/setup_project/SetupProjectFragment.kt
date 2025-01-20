package com.ndmq.android_mvvm_project_base.presentation.setup_project

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.databinding.FragmentSetupProjectBinding
import com.ndmq.android_mvvm_project_base.utils.extension.navigate
import com.ndmq.android_mvvm_project_base.utils.extension.observeNavigationResultOnce
import com.ndmq.android_mvvm_project_base.utils.extension.observeNotNull
import com.ndmq.android_mvvm_project_base.utils.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupProjectFragment : BaseFragment<FragmentSetupProjectBinding>(
    FragmentSetupProjectBinding::inflate
) {

    private val viewModel by viewModels<SetupProjectViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleEvent()

        observeFragmentResult()
        observeViewModel()
    }


    private fun handleEvent() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.projectName.value = binding.edtProjectName.text.toString()
        binding.edtProjectName.doOnTextChanged { text, _, _, _ ->
            viewModel.projectName.value = text.toString()
        }

        binding.btnCaptureBackgroundImage.setOnClickListener {

        }

        binding.btnChooseBackgroundImage.setOnClickListener {
            navigate(R.id.action_setupProjectFragment_to_chooseBackgroundFragment)
        }

        binding.btnChooseBackgroundColor.setOnClickListener {
            navigate(
                R.id.action_setupProjectFragment_to_colorPickerDialog,
                Bundle().apply {
                    putString("RETURN_COLOR_FOR", "BACKGROUND_COLOR")
                    putInt("CURRENT_COLOR", viewModel.backgroundColor.value ?: Color.WHITE)
                }
            )
        }

        binding.llOutputFormat.setOnClickListener {
            navigate(
                R.id.action_setupProjectFragment_to_chooseFormatFragment,
                bundleOf("CURRENT_FORMAT" to viewModel.outputFormat.value)
            )
        }

        binding.llCanvasSize.setOnClickListener {
            navigate(
                R.id.action_setupProjectFragment_to_chooseCanvasSizeFragment,
                bundleOf("CURRENT_CANVAS_SIZE" to viewModel.widthHeightRatio.value)
            )
        }

        binding.llFps.setOnClickListener {
            navigate(
                R.id.action_setupProjectFragment_to_chooseFpsFragment,
                bundleOf("CURRENT_FPS" to viewModel.fps.value)
            )
        }

        binding.btnCreateProject.setOnClickListener {
            viewModel.createProject(onProjectCreated = { id, createdTime ->
                navigate(
                    R.id.action_setUpProjectFragment_to_drawFragment,
                    Bundle().apply {
                        putString("PROJECT_FOLDER_NAME", createdTime.toString())
                        putLong("PROJECT_ID", id)
                    }
                )
            })
        }
    }

    private fun observeFragmentResult() {
        observeNavigationResultOnce<Int>("BACKGROUND_COLOR") {
            viewModel.backgroundColor.value = it
            viewModel.backgroundPath.value = null
        }

        observeNavigationResultOnce<String>("BACKGROUND_PATH") {
            viewModel.backgroundPath.value = it
            viewModel.backgroundColor.value = null
        }

        observeNavigationResultOnce<String>("OUTPUT_FORMAT") {
            viewModel.outputFormat.value = it
        }

        observeNavigationResultOnce<Float>("CANVAS_SIZE") {
            viewModel.widthHeightRatio.value = it
        }

        observeNavigationResultOnce<Int>("FPS") {
            viewModel.fps.value = it
        }
    }

    private fun observeViewModel() {
        viewModel.message.observe(viewLifecycleOwner) {
            context?.showToast(it)
        }

        viewModel.projectName.observe(viewLifecycleOwner) {
            binding.btnCreateProject.isEnabled = it.isNotEmpty()
        }

        viewModel.backgroundColor.observeNotNull(viewLifecycleOwner) {
            binding.ivBackgroundColor.visibility = View.VISIBLE
            binding.ivBackgroundImage.visibility = View.INVISIBLE
            binding.ivBackgroundColor.setBackgroundColor(it)
        }

        viewModel.backgroundPath.observeNotNull(viewLifecycleOwner) {
            binding.ivBackgroundColor.visibility = View.INVISIBLE
            binding.ivBackgroundImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(it).into(binding.ivBackgroundImage)
        }

        viewModel.outputFormat.observeNotNull(viewLifecycleOwner) {
            binding.tvOutputFormat.text = it
        }

        viewModel.widthHeightRatio.observe(viewLifecycleOwner) {
            binding.tvCanvasSize.text = mapStringCanvasSize(it)
        }

        viewModel.fps.observeNotNull(viewLifecycleOwner) {
            binding.tvFps.text = it.toString()
        }
    }

    private fun mapStringCanvasSize(ratio: Float): String {
        return when (ratio) {
            1f / 1 -> "1:1"
            16f / 9 -> "16:9"
            4f / 3 -> "4:3"
            else -> ""
        }
    }
}