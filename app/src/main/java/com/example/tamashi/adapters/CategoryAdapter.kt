package com.example.tamashi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamashi.R
import com.example.tamashi.models.CategoryModel

class CategoryAdapter : ListAdapter<CategoryModel, CategoryAdapter.ViewHolder>(DiffCallback()){

    class DiffCallback: DiffUtil.ItemCallback<CategoryModel>()
    {
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val categoryName : TextView = itemView.findViewById(R.id.category_list_name)
        fun bindData(category: CategoryModel) {
            categoryName.text = category.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.category_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bindData(category)
    }
}