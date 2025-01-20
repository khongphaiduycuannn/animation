package com.ndmq.android_mvvm_project_base.presentation.setup_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.ndmq.android_mvvm_project_base.data.model.Background
import com.ndmq.android_mvvm_project_base.data.model.CategoryBackgrounds
import com.ndmq.android_mvvm_project_base.databinding.ItemChooseBackgroundBinding
import com.ndmq.android_mvvm_project_base.databinding.ItemChooseBackgroundTitleBinding

class BackgroundAdapter : Adapter<RecyclerView.ViewHolder>() {

    val items = mutableListOf<Any>()

    fun setItemsData(data: List<CategoryBackgrounds>) {
        items.clear()
        data.forEach { category ->
            items.add(category.categoryName)
            items.addAll(category.backgrounds)
        }
        notifyDataSetChanged()
    }

    var onBackgroundClick: ((Background) -> Unit)? = null


    inner class TitleViewHolder(
        val binding: ItemChooseBackgroundTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(title: String) {
            binding.tvBackgroundCategory.text = title
        }
    }

    inner class BackgroundViewHolder(
        val binding: ItemChooseBackgroundBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(background: Background) {
            Glide.with(binding.root).load(background.path).into(binding.tvBackground)

            binding.root.setOnClickListener {
                onBackgroundClick?.invoke(background)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) TITLE_TYPE else BACKGROUND_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TITLE_TYPE -> {
                val binding = ItemChooseBackgroundTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TitleViewHolder(binding)
            }

            else -> {
                val binding = ItemChooseBackgroundBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                BackgroundViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TitleViewHolder -> {
                holder.onBind(items[position] as String)
            }

            is BackgroundViewHolder -> {
                holder.onBind(items[position] as Background)
            }
        }
    }


    companion object {
        const val TITLE_TYPE = 1

        const val BACKGROUND_TYPE = 2
    }
}