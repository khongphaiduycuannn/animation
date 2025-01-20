package com.ndmq.android_mvvm_project_base.presentation.setup_project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ndmq.android_mvvm_project_base.databinding.ItemChooseFpsBinding

class FpsAdapter() : Adapter<FpsAdapter.FpsViewHolder>() {

    inner class FpsViewHolder(val binding: ItemChooseFpsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: Int?, index: Int) {
            binding.tvFps.text = item?.toString() ?: ""

            binding.tvFps.setOnClickListener {
                onItemClick?.invoke(item ?: 24, index)
            }
        }
    }

    var items = mutableListOf<Int?>()
        set(value) {
            items.clear()
            items.addAll(value)
            items.addAll(listOf(null, null, null, null))
            notifyDataSetChanged()
        }

    var onItemClick: ((item: Int, index: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FpsViewHolder {
        val binding =
            ItemChooseFpsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FpsViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FpsViewHolder, position: Int) {
        holder.onBind(items[position], position)
    }
}