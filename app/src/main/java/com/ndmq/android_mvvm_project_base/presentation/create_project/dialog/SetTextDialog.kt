package com.ndmq.android_mvvm_project_base.presentation.create_project.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.databinding.DialogSetTextBinding
import com.ndmq.android_mvvm_project_base.utils.extension.setNavigationResult

class SetTextDialog : DialogFragment() {

    private val binding by lazy { DialogSetTextBinding.inflate(layoutInflater) }

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
        binding.btnConfirm.setOnClickListener {
            val text = binding.edtStickerText.text.toString()
            if (text.isNotBlank()) {
                setNavigationResult("STICKER_TEXT", text)
                findNavController().navigateUp()
            }
        }
    }
}