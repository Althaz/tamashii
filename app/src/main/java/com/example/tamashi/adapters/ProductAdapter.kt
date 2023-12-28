package com.example.tamashi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tamashi.PreferencesManager
import com.example.tamashi.R
import com.example.tamashi.models.ProductModel

class ProductAdapter :
    ListAdapter<ProductModel, ProductAdapter.ViewHolder>(DiffCallback())
{
    class DiffCallback: DiffUtil.ItemCallback<ProductModel>()
    {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val productImage : ImageView = itemView.findViewById(R.id.product_card_img)
        val productTitle : TextView = itemView.findViewById(R.id.product_card_title)
        val productPrice : TextView = itemView.findViewById(R.id.product_card_price)
        fun bindData(product: ProductModel){
            Glide.with(productImage)
                .load(PreferencesManager.URL + product.thumbnail)
                .into(productImage)
            Toast.makeText(itemView.context, product.thumbnail, Toast.LENGTH_LONG)
            productTitle.text = product.name
            productPrice.text = "Rp. " + product.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.bindData(product)
    }
}