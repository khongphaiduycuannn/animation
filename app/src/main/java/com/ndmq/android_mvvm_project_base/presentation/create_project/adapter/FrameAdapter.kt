package com.ndmq.android_mvvm_project_base.presentation.create_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.DrawView
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.constants.ViewConstants.DEFAULT_WIDTH_PIXEL
import com.ndmq.android_mvvm_project_base.data.model.Frame
import com.ndmq.android_mvvm_project_base.databinding.ItemDrawFrameAddBinding
import com.ndmq.android_mvvm_project_base.databinding.ItemDrawFrameBinding

class FrameAdapter(val context: Context) : Adapter<ViewHolder>() {

    private val frames = mutableListOf<Frame>()

    private val drawViews = mutableListOf<DrawView>()


    fun setFrames(frames: List<Frame>) {
        drawViews.forEach {
            it.recycle()
        }
        drawViews.clear()

        this.frames.clear()
        frames.forEach { frame ->
            this.frames.add(frame)
            drawViews.add(DrawView(context).apply { setBitmapWidthHeight(DEFAULT_WIDTH_PIXEL, DEFAULT_WIDTH_PIXEL) })
        }
        notifyDataSetChanged()
    }

    var onFrameItemClick: ((Frame, index: Int) -> Unit)? = null

    var onAddItemClick: (() -> Unit)? = null


    inner class FrameViewHolder(
        private val binding: ItemDrawFrameBinding
    ) : ViewHolder(binding.root) {

        fun onBind(frame: Frame, index: Int) {
            binding.tvFrameCount.text = "${index + 1}"

            drawViews[index].frame = frame
            binding.ivFrame.setImageBitmap(drawViews[index].getDrawBitmap())

            binding.root.setOnClickListener {
                onFrameItemClick?.invoke(frame, index)
            }
        }
    }

    inner class AddViewHolder(
        private val binding: ItemDrawFrameAddBinding
    ) : ViewHolder(binding.root) {

        fun onBind() {
            binding.root.setOnClickListener {
                onAddItemClick?.invoke()
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position >= frames.size) {
            ADD_TYPE
        } else {
            FRAME_TYPE
        }
    }

    override fun getItemCount(): Int = frames.size + 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is FrameViewHolder -> holder.onBind(frames[position], position)
            is AddViewHolder -> holder.onBind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            FRAME_TYPE -> {
                FrameViewHolder(
                    ItemDrawFrameBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            else -> {
                AddViewHolder(
                    ItemDrawFrameAddBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    companion object {
        const val FRAME_TYPE = 1

        const val ADD_TYPE = 2
    }
}