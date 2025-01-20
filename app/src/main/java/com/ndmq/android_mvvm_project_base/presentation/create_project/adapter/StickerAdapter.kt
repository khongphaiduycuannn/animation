package com.ndmq.android_mvvm_project_base.presentation.create_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ndmq.android_mvvm_project_base.data.model.Sticker
import com.ndmq.android_mvvm_project_base.databinding.ItemStickerBinding

class StickerAdapter(
    var onItemClick: (Sticker) -> Unit = {}
) : RecyclerView.Adapter<StickerAdapter.StickerViewHolder>() {

    private val stickers = mutableListOf<Sticker>()

    fun setStickers(stickers: List<Sticker>) {
        this.stickers.clear()
        this.stickers.addAll(stickers)
        notifyDataSetChanged()
    }

    inner class StickerViewHolder(
        private val binding: ItemStickerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(sticker: Sticker) {
            Glide.with(binding.root).load(sticker.path).into(binding.ivSticker)

            binding.root.setOnClickListener {
                onItemClick(sticker)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StickerAdapter.StickerViewHolder {
        val binding = ItemStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StickerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StickerAdapter.StickerViewHolder, position: Int) {
        holder.onBind(stickers[position])
    }

    override fun getItemCount(): Int = stickers.size
}