package com.example.tamashi.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tamashi.PreferencesManager
import com.example.tamashi.PreferencesManager.Companion.SHARED_PREFS
import com.example.tamashi.R
import com.example.tamashi.models.OrderModel

class OrderAdapter: ListAdapter<OrderModel, OrderAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback: DiffUtil.ItemCallback<OrderModel>()
    {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val sharedPrefs: SharedPreferences = itemView.context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val orderImg: ImageView = itemView.findViewById(R.id.order_img)
        val orderName: TextView = itemView.findViewById(R.id.order_name_tv)
        val orderPrice: TextView = itemView.findViewById(R.id.order_price_tv)
        val orderTotal: TextView = itemView.findViewById(R.id.order_total_tv)
        val orderQty: TextView = itemView.findViewById(R.id.order_qty_tv)
        val orderStatus: TextView = itemView.findViewById(R.id.order_status_tv)
        fun bindData(orders: OrderModel?) {
            Glide.with(orderImg)
                .load(PreferencesManager.URL + orders?.thumbnail)
                .into(orderImg)
            orderName.text = orders?.name
            orderPrice.text = "Price: Rp. " + orders?.price
            orderTotal.text = "Total: Rp. " + orders?.total_price
            orderQty.text = "QTY: " + orders?.qty
            orderStatus.text = "Status: " + orders?.status

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.order_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orders = getItem(position)
        holder.bindData(orders)
    }
}