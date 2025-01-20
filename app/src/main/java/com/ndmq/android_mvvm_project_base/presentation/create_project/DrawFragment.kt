package com.ndmq.android_mvvm_project_base.presentation.create_project

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.marginEnd
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bumptech.glide.Glide
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.DrawView
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.callback.OnDrawActionInvokeListener
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson.GsonConfig
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.DrawAction
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.ShapeType
import com.ndmq.android_mvvm_project_base.custom_view.sticker_view.TextSticker
import com.ndmq.android_mvvm_project_base.data.model.Frame
import com.ndmq.android_mvvm_project_base.data.model.Sticker
import com.ndmq.android_mvvm_project_base.databinding.FragmentDrawBinding
import com.ndmq.android_mvvm_project_base.presentation.MainActivity
import com.ndmq.android_mvvm_project_base.presentation.create_project.adapter.FrameAdapter
import com.ndmq.android_mvvm_project_base.presentation.create_project.dialog.ChooseStickerPopup
import com.ndmq.android_mvvm_project_base.utils.PermissionUtils
import com.ndmq.android_mvvm_project_base.utils.extension.logd
import com.ndmq.android_mvvm_project_base.utils.extension.navigate
import com.ndmq.android_mvvm_project_base.utils.extension.observeNavigationResultOnce
import com.ndmq.android_mvvm_project_base.utils.extension.observeNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class DrawFragment : BaseFragment<FragmentDrawBinding>(
    FragmentDrawBinding::inflate
) {

    private val viewModel by viewModels<DrawViewModel>()


    private val chooseStickerPopup by lazy { ChooseStickerPopup(requireContext()) }


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null && uri.path != null) {
                navigate(
                    R.id.action_drawFragment_to_removeImageBackgroundFragment,
                    Bundle().apply {
                        putParcelable("SELECTED_IMAGE_PATH", uri)
                        putString("PROJECT_FOLDER_NAME", viewModel.projectFolderName)
                    }
                )
            }
        }


    private val frameAdapter by lazy { FrameAdapter(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()

        initView()

        handleEvent()

        observeData()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.stickerView.removeAllStickers()
        chooseStickerPopup.dismiss()
    }


    private fun initData() {
        arguments?.apply {
            viewModel.projectFolderName = getString("PROJECT_FOLDER_NAME") ?: ""
            val projectId = getLong("PROJECT_ID")
            viewModel.getProject(projectId)
            clear()
        }
    }


    private fun initView() {
        binding.stickerView.isLocked = true

        binding.rclFrames.adapter = frameAdapter
    }


    private fun handleEvent() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnExport.setOnClickListener {
            navigate(R.id.action_drawFragment_to_exportFragment)
        }

        binding.btnPreviewProject.setOnClickListener {
            navigate(
                R.id.action_drawFragment_to_previewFragment,
                bundleOf("PROJECT_ID" to viewModel.newestProject?.id)
            )
        }

        handleBrushEvent()

        handleEraseEvent()

        handleFillColorEvent()

        handleDropColorEvent()

        handleShapeEvent()

        handleTextEvent()

        handleStickerEvent()

        handleCropImageEvent()

        handleUndoRedoEvent()

        handleSelectFrameEvent()
    }


    private fun observeData() {
        observeDrawEvent()

        observeViewModel()

        observeNavigationResult()
    }

    private fun observeDrawEvent() {
        binding.drawView.setOnDrawActionInvokeListener(object : OnDrawActionInvokeListener {
            override fun onDrawActionInvoke(action: DrawAction?) {
                viewModel.updateCurrentFrame(binding.drawView.frame)
            }
        })
    }


    private fun observeNavigationResult() {
        observeNavigationResultOnce<Int>("BRUSH_COLOR") {
            viewModel.brushColor.value = it
        }

        observeNavigationResultOnce<Int>("FILL_COLOR") {
            viewModel.fillColor.value = it
        }

        observeNavigationResultOnce<Int>("SHAPE_COLOR") {
            viewModel.shapeColor.value = it
        }

        observeNavigationResultOnce<Int>("TEXT_COLOR") {
            viewModel.textColor.value = it
        }

        observeNavigationResultOnce<String>("STICKER_TEXT") {
            binding.stickerView.isLocked = false

            val textSticker = TextSticker(requireContext()).apply {
                setText(it)
                setTextColor(viewModel.textColor.value ?: Color.BLACK)
                resizeText()
            }

            binding.stickerView.addSticker(textSticker)
        }

        observeNavigationResultOnce<String>("REMOVED_BACKGROUND_IMAGE_PATH") {
            viewModel.currentMode.value = DrawView.Mode.IMAGE_STICKER
            viewModel.currentSticker.value = Sticker(it, "free")
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentProject?.collect {
                val backgroundColor = it.backgroundColor
                val backgroundPath = it.backgroundPath
                val outputFormat = it.outputFormat
                val widthHeightRatio = it.widthHeightRatio
                val fps = it.fps
                val frames =
                    GsonConfig.getConfigurationGson().fromJson(it.frames, Array<Frame>::class.java)
                        .toList()

                viewModel.newestProject = it

                viewModel.frames.value = frames.toMutableList()

                val heightWidthRatio = 1f / widthHeightRatio
                binding.ivColorBackground.heightWidthRatio = heightWidthRatio
                binding.ivImageBackground.heightWidthRatio = heightWidthRatio
                binding.gridView.heightWidthRatio = heightWidthRatio
                binding.stickerView.heightWidthRatio = heightWidthRatio
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

                binding.tvProjectFormat.text =
                    "$outputFormat ${mapRatioDimenToString(widthHeightRatio)} $fps FPS"
            }
        }


        viewModel.frames.observe(viewLifecycleOwner) {
            frameAdapter.setFrames(it)
        }


        viewModel.currentMode.observeNotNull(viewLifecycleOwner) { mode ->
            binding.drawView.currMode = mode
            removeToolItemView()
            when (mode) {
                DrawView.Mode.BRUSH -> {
                    binding.btnBrush.setBackgroundColor(Color.parseColor("#aabbcc"))
                    binding.llBrushTools.visibility = View.VISIBLE
                }

                DrawView.Mode.ERASE -> {
                    removeToolItemView()
                    binding.btnErase.setBackgroundColor(Color.parseColor("#aabbcc"))
                    binding.llEraseTools.visibility = View.VISIBLE
                }

                DrawView.Mode.FILL -> {
                    removeToolItemView()
                    binding.btnFillColor.setBackgroundColor(Color.parseColor("#aabbcc"))
                    binding.llFillColorTools.visibility = View.VISIBLE
                }

                DrawView.Mode.DROP_COLOR -> {
                    removeToolItemView()
                    binding.btnDropColor.setBackgroundColor(Color.parseColor("#aabbcc"))
                    binding.llDropColorTools.visibility = View.VISIBLE
                }

                DrawView.Mode.SHAPE -> {
                    removeToolItemView()
                    binding.btnShape.setBackgroundColor(Color.parseColor("#aabbcc"))
                    binding.llShapeTools.visibility = View.VISIBLE
                }

                DrawView.Mode.IMAGE_STICKER -> {
                    removeToolItemView()
                    binding.btnSticker.setBackgroundColor(Color.parseColor("#aabbcc"))
                }

                DrawView.Mode.TEXT -> {
                    removeToolItemView()
                    binding.btnText.setBackgroundColor(Color.parseColor("#aabbcc"))
                    binding.llTextTools.visibility = View.VISIBLE
                }
            }
        }


        viewModel.brushWidth.observe(viewLifecycleOwner) { progress ->
            binding.drawView.setBrushWidth(progress.toFloat())
            binding.tvBrushWidth.text = "${progress}px"
        }

        viewModel.brushColor.observe(viewLifecycleOwner) { color ->
            binding.btnSelectBrushColor.setBackgroundColor(color)
            binding.drawView.setBrushColor(color)
        }


        viewModel.eraseWidth.observe(viewLifecycleOwner) { progress ->
            binding.drawView.setEraseWidth(progress.toFloat())
            binding.tvEraseWidth.text = "${progress}px"
        }


        viewModel.fillColor.observe(viewLifecycleOwner) {
            binding.btnSelectFillColor.setBackgroundColor(it)
            binding.drawView.setFillColor(it)
        }


        viewModel.dropColor.observe(viewLifecycleOwner) {
            binding.viewDropColor.setBackgroundColor(it)
            viewModel.brushColor.value = it
            viewModel.fillColor.value = it
        }

        viewModel.shapeWidth.observe(viewLifecycleOwner) {
            binding.drawView.setShapeWidth(it.toFloat())
            binding.tvShapeWidth.text = "${it}px"
        }

        viewModel.shapeColor.observe(viewLifecycleOwner) {
            binding.btnSelectShapeColor.setBackgroundColor(it)
            binding.drawView.setShapeColor(it)
        }

        viewModel.currentShape.observe(viewLifecycleOwner) {
            binding.drawView.setCurrentShape(it)
            if (it == ShapeType.RECTANGLE) {
                binding.btnShapeRect.setBackgroundColor(Color.parseColor("#aabbcc"))
                binding.btnShapeOval.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
            } else {
                binding.btnShapeRect.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                binding.btnShapeOval.setBackgroundColor(Color.parseColor("#aabbcc"))
            }
        }


        viewModel.currentSticker.observe(viewLifecycleOwner) {
            binding.drawView.setCurrentSticker(it.path)
        }


        viewModel.textColor.observe(viewLifecycleOwner) {
            binding.btnSelectTextColor.setBackgroundColor(it)
        }
    }


    private fun handleBrushEvent() {
        binding.btnBrush.setOnClickListener {
            viewModel.currentMode.value = DrawView.Mode.BRUSH
        }

        binding.btnSelectBrushColor.setOnClickListener {
            navigate(
                R.id.action_drawFragment_to_colorPickerDialog,
                bundleOf("RETURN_COLOR_FOR" to "BRUSH_COLOR")
            )
        }

        binding.sbBrushWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.brushWidth.value = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun handleEraseEvent() {
        binding.btnErase.setOnClickListener {
            viewModel.currentMode.value = DrawView.Mode.ERASE
        }

        binding.sbEraseWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.eraseWidth.value = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun handleFillColorEvent() {
        binding.btnFillColor.setOnClickListener {
            viewModel.currentMode.value = DrawView.Mode.FILL
        }

        binding.btnSelectFillColor.setOnClickListener {
            navigate(
                R.id.action_drawFragment_to_colorPickerDialog,
                bundleOf("RETURN_COLOR_FOR" to "FILL_COLOR")
            )
        }
    }

    private fun handleDropColorEvent() {
        binding.btnDropColor.setOnClickListener {
            viewModel.currentMode.value = DrawView.Mode.DROP_COLOR
        }

        binding.drawView.setOnDrawActionInvokeListener(object : OnDrawActionInvokeListener {
            override fun onDropColorActionInvoke(color: Int) {
                viewModel.dropColor.value = color
            }
        })
    }

    private fun handleUndoRedoEvent() {
        binding.btnUndo.setOnClickListener {
            binding.drawView.undo()
        }

        binding.btnRedo.setOnClickListener {
            binding.drawView.redo()
        }
    }

    private fun handleShapeEvent() {
        binding.btnShape.setOnClickListener {
            viewModel.currentMode.value = DrawView.Mode.SHAPE
        }

        binding.btnSelectShapeColor.setOnClickListener {
            navigate(
                R.id.action_drawFragment_to_colorPickerDialog,
                bundleOf("RETURN_COLOR_FOR" to "SHAPE_COLOR")
            )
        }

        binding.sbShapeWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.shapeWidth.value = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnShapeRect.setOnClickListener {
            viewModel.currentShape.value = ShapeType.RECTANGLE
        }

        binding.btnShapeOval.setOnClickListener {
            viewModel.currentShape.value = ShapeType.OVAL
        }
    }

    private fun handleTextEvent() {
        binding.btnText.setOnClickListener {
            viewModel.currentMode.value = DrawView.Mode.TEXT
        }

        binding.btnSelectTextColor.setOnClickListener {
            navigate(
                R.id.action_drawFragment_to_colorPickerDialog,
                bundleOf("RETURN_COLOR_FOR" to "TEXT_COLOR")
            )
        }

        binding.tvAddText.setOnClickListener {
            navigate(R.id.action_drawFragment_to_setTextDialog)
        }

        binding.stickerView.setOnTouchOutsideListener { textSticker ->
            if (textSticker != null) {
                drawTextSticker(textSticker as TextSticker)
                binding.stickerView.isLocked = true
                binding.stickerView.removeAllStickers()
            }
        }
    }

    private fun handleStickerEvent() {
        chooseStickerPopup.onItemClick = { sticker ->
            viewModel.currentMode.value = DrawView.Mode.IMAGE_STICKER
            viewModel.currentSticker.value = sticker
            chooseStickerPopup.dismiss()
        }

        chooseStickerPopup.setOnDismissListener {
            lifecycleScope.launch {
                delay(100)
                binding.drawView.isLooked = false
            }
        }

        binding.btnSticker.setOnClickListener {
            if (!chooseStickerPopup.isShowing) {
                binding.drawView.isLooked = true
                chooseStickerPopup.showAsDropDown(binding.vStickerDropdownView, 0, 0, Gravity.START)
            } else {
                chooseStickerPopup.dismiss()
            }
        }
    }

    private fun handleCropImageEvent() {
        binding.btnImage.setOnClickListener {
            val activity = requireActivity() as MainActivity
            PermissionUtils(activity).requestPermission(
                listOf(
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        READ_EXTERNAL_STORAGE
                    } else {
                        READ_MEDIA_IMAGES
                    }
                ),
                callback = object : PermissionUtils.PermissionCallback {
                    override fun onAllGranted(permission: List<String>) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                    override fun onDenied(listGranted: List<String>, listDenied: List<String>) {}

                    override fun onShouldOpenSetting(callback: PermissionUtils.IOpenSettingCallback) {
                        callback.invoke()
                    }
                }
            )
        }
    }

    private fun handleSelectFrameEvent() {
        frameAdapter.onFrameItemClick = { _, index ->
            val layoutManger = binding.rclFrames.layoutManager as LinearLayoutManager
            val itemWidth = binding.rclFrames.getChildAt(0).width
            val itemMargin = binding.rclFrames.getChildAt(0).marginEnd
            val currentPosition = layoutManger.findFirstVisibleItemPosition()
            binding.rclFrames.smoothScrollBy(itemWidth * (index - currentPosition) + itemMargin, 0)
        }

        frameAdapter.onAddItemClick = {
            viewModel.addNewFrame()
        }

        binding.rclFrames.addOnScrollListener(getScrollListener())
    }


    private fun removeToolItemView() {
        binding.llPaintTools.children.forEach { view ->
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        }

        binding.llBrushTools.visibility = View.GONE
        binding.llEraseTools.visibility = View.GONE
        binding.llFillColorTools.visibility = View.GONE
        binding.llDropColorTools.visibility = View.GONE
        binding.llShapeTools.visibility = View.GONE
        binding.llTextTools.visibility = View.GONE
    }

    private fun drawTextSticker(textSticker: TextSticker) {
        textSticker.apply {
            val sticker = DrawAction.Text(
                translationX,
                translationY,
                textSizePixels,
                width,
                currentScale,
                currentAngle,
                viewModel.textColor.value ?: Color.BLACK,
                text.toString()
            )
            binding.drawView.addText(sticker)
        }
    }

    private fun mapRatioDimenToString(radio: Float): String {
        return when (radio) {
            1f / 1 -> "1:1"
            16f / 9 -> "16:9"
            4f / 3 -> "4:3"
            else -> "1:1"
        }
    }

    private fun getScrollListener() = object : OnScrollListener() {

        private var previousVisibleItemPosition = -1

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (layoutManager != null) {
                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    val firstVisibleView =
                        layoutManager.findViewByPosition(firstVisiblePosition)

                    if (firstVisibleView != null) {
                        val viewStart = firstVisibleView.left
                        val viewEnd = firstVisibleView.right + firstVisibleView.marginEnd
                        val itemWidth = firstVisibleView.width

                        var offSet = if (itemWidth / 2 < abs(viewStart)) {
                            viewEnd
                        } else {
                            viewStart
                        }
                        if (viewEnd <= 5 || viewStart >= -5) {
                            offSet = 0
                        }

                        if (offSet == 0) {
                            viewModel.currentFrameIndex = firstVisiblePosition
                        }

                        drawFrame(firstVisiblePosition)

                        recyclerView.smoothScrollBy(offSet, 0)
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
            layoutManager?.findFirstVisibleItemPosition()?.let { firstVisiblePosition ->
                drawFrame(firstVisiblePosition)
            }
        }

        private fun drawFrame(firstVisiblePosition: Int) {
            if (firstVisiblePosition == previousVisibleItemPosition) {
                return
            }

            val frames = viewModel.frames.value ?: return
            if (frames.size <= firstVisiblePosition) {
                return
            }

            val frame = frames[firstVisiblePosition]
            binding.drawView.frame = frame
            previousVisibleItemPosition = firstVisiblePosition

            if (firstVisiblePosition > 0) {
                val prevIndexFrame =
                    viewModel.frames.value?.get(firstVisiblePosition - 1)
                prevIndexFrame?.let {
                    binding.previewDrawView.frame = prevIndexFrame
                }
            } else {
                binding.previewDrawView.frame = Frame.EMPTY_FRAME
            }
        }
    }
}