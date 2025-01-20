package com.ndmq.android_mvvm_project_base.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import com.ndmq.android_mvvm_project_base.databinding.ItemHomeProjectBinding

class ProjectAdapter : Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(
        private val binding: ItemHomeProjectBinding
    ) : ViewHolder(binding.root) {

        fun onBind(project: DrawProject) {

        }
    }


    private val projects = mutableListOf<DrawProject>()

    fun setData(projects: List<DrawProject>) {
        this.projects.clear()
        this.projects.addAll(projects)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding =
            ItemHomeProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun getItemCount(): Int = projects.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.onBind(projects[position])
    }
}