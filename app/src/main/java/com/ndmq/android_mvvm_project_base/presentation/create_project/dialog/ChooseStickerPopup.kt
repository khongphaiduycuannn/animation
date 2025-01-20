package com.ndmq.android_mvvm_project_base.presentation.create_project.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import com.ndmq.android_mvvm_project_base.data.model.Sticker
import com.ndmq.android_mvvm_project_base.data.source.asset.StickerUtils
import com.ndmq.android_mvvm_project_base.databinding.PopupChooseStickerBinding
import com.ndmq.android_mvvm_project_base.presentation.create_project.adapter.StickerAdapter

class ChooseStickerPopup(context: Context) : PopupWindow() {

    private val binding: PopupChooseStickerBinding = PopupChooseStickerBinding.inflate(
        LayoutInflater.from(context)
    )

    private val adapter = StickerAdapter()

    var onItemClick: ((Sticker) -> Unit)? = null

    init {
        contentView = binding.root
        width = WRAP_CONTENT
        height = 500
        isOutsideTouchable = true

        val stickers = StickerUtils.getStickersFromAssets(context)
        binding.rclStickers.adapter = adapter
        adapter.setStickers(stickers)

        adapter.onItemClick = { sticker ->
            this.onItemClick?.invoke(sticker)
        }
    }
}