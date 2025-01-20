package com.ndmq.android_mvvm_project_base.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.databinding.DialogColorPickerBinding
import com.ndmq.android_mvvm_project_base.utils.extension.setNavigationResult

class ColorPickerDialog : DialogFragment() {

    private val binding by lazy { DialogColorPickerBinding.inflate(layoutInflater) }

    private var selectedColor = Color.BLACK

    private var returnColorFor = ""


    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setBackgroundDrawableResource(R.color.transparent)
            attributes.apply {
                gravity = Gravity.CENTER
            }
            setDimAmount(0.15f)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()

        handleEvent()
    }


    private fun initView() {
        arguments?.getInt("CURRENT_COLOR")?.let {
            selectedColor = it
        }

        arguments?.getString("RETURN_COLOR_FOR")?.let {
            returnColorFor = it
        }

        setColorInformationView(selectedColor)
    }

    private fun handleEvent() {
        binding.colorPicker.setOnColorChangedListener { color ->
            setColorInformationView(color)
        }

        binding.btnConfirm.setOnClickListener {
            setNavigationResult(returnColorFor, selectedColor)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }


    private fun setColorInformationView(color: Int) {
        val hexString = "#${
            String.format("#%06X", color).let {
                it.substring(it.length - 6)
            }
        }"

        selectedColor = color
        binding.tvColorHex.text = hexString
        binding.cvColor.setCardBackgroundColor(color)

        binding.tvColorRed.text = Integer.parseInt(hexString.substring(1, 3), 16).toString()
        binding.tvColorGreen.text = Integer.parseInt(hexString.substring(3, 5), 16).toString()
        binding.tvColorBlue.text = Integer.parseInt(hexString.substring(5, 7), 16).toString()
    }
}