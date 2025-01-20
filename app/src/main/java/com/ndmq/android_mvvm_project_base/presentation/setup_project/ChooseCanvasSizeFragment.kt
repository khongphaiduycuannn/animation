package com.ndmq.android_mvvm_project_base.presentation.setup_project

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import com.ndmq.android_mvvm_project_base.base.BaseFragment
import com.ndmq.android_mvvm_project_base.databinding.FragmentChooseCanvasSizeBinding
import com.ndmq.android_mvvm_project_base.utils.extension.setNavigationResult

class ChooseCanvasSizeFragment : BaseFragment<FragmentChooseCanvasSizeBinding>(
    FragmentChooseCanvasSizeBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()

        handleEvent()
    }

    private fun initView() {
        arguments?.getFloat("CURRENT_CANVAS_SIZE")?.let {
            setRadioButtonView(it, 0)
        }
    }

    private fun handleEvent() {
        binding.llCanvas11.setOnClickListener {
            val ratio = 1f / 1
            setRadioButtonView(ratio)
            setNavigationResult("CANVAS_SIZE", ratio)
        }

        binding.llCanvas169.setOnClickListener {
            val ratio = 16f / 9
            setRadioButtonView(ratio)
            setNavigationResult("CANVAS_SIZE", ratio)
        }

        binding.llCanvas43.setOnClickListener {
            val ratio = 4f / 3
            setRadioButtonView(ratio)
            setNavigationResult("CANVAS_SIZE", ratio)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun setRadioButtonView(ratio: Float, duration: Long = ANIM_DURATION) {
        binding.rb11Format.isChecked = false
        binding.rb169Format.isChecked = false
        binding.rb43Format.isChecked = false

        when (ratio) {
            1f / 1 -> binding.rb11Format.isChecked = true
            16f / 9 -> binding.rb169Format.isChecked = true
            4f / 3 -> binding.rb43Format.isChecked = true
        }

        updateCanvasSize(ratio, duration)
    }

    private fun updateCanvasSize(ratio: Float, duration: Long = ANIM_DURATION) {
        val width = binding.ivCanvas.width
        val height = binding.ivCanvas.height
        val newHeight = (width / ratio).toInt()

        val resizeAnimator = ValueAnimator.ofInt(height, newHeight)
        resizeAnimator.addUpdateListener { anim ->
            binding.ivCanvas.updateLayoutParams {
                this.height = anim.animatedValue as Int
            }
        }

        resizeAnimator.doOnEnd {
            (binding.ivCanvas.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                mapRatioDimenToString(ratio)
        }
        resizeAnimator.duration = duration
        resizeAnimator.start()
    }

    private fun mapRatioDimenToString(radio: Float): String {
        return when (radio) {
            1f / 1 -> "1:1"
            16f / 9 -> "16:9"
            4f / 3 -> "4:3"
            else -> "1:1"
        }
    }

    companion object {
        const val ANIM_DURATION = 300L
    }
}