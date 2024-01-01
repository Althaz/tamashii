package com.example.tamashi.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.tamashi.DetailActivity
import com.example.tamashi.PreferencesManager
import com.example.tamashi.PreferencesManager.Companion.JWT
import com.example.tamashi.PreferencesManager.Companion.SHARED_PREFS
import com.example.tamashi.R
import com.example.tamashi.models.WishlistModel

class WishlistAdapter: ListAdapter<WishlistModel, WishlistAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback: DiffUtil.ItemCallback<WishlistModel>()
    {
        override fun areItemsTheSame(oldItem: WishlistModel, newItem: WishlistModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WishlistModel, newItem: WishlistModel): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val thumbnailImg: ImageView = itemView.findViewById(R.id.wishlist_img)
        val productName: TextView = itemView.findViewById(R.id.wishlist_name_tv)
        val productPrice: TextView = itemView.findViewById(R.id.wishlist_price_tv)
        val deleteBtn: ImageView = itemView.findViewById(R.id.wishlist_delete_btn)
        val sharedPrefs: SharedPreferences = itemView.context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        fun bindData(wishlist: WishlistModel?) {
            Glide.with(thumbnailImg)
                .load(PreferencesManager.URL + wishlist?.thumbnail)
                .into(thumbnailImg)
            productName.text = wishlist?.name
            productPrice.text = wishlist?.price.toString()
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("id_produk", wishlist?.id_produk.toString())
                itemView.context.startActivity(intent)
            }

            deleteBtn.setOnClickListener{
                deleteWishlist(wishlist?.id)
            }
        }

        private fun deleteWishlist(id: Int?) {
            val queue = Volley.newRequestQueue(itemView.context)
            val url = PreferencesManager.URL + "/api/wishlists/" + id

            val stringRequest = object : StringRequest(Request.Method.DELETE, url,
                Response.Listener { response ->
                    Toast.makeText(itemView.context, "Deleted", Toast.LENGTH_LONG).show()
                },
                Response.ErrorListener { error ->
                    Log.e("RES ERROR", error.message.toString())
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer " + sharedPrefs.getString(JWT, "")
                    return headers
                }
            }
            queue.add(stringRequest)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.wishlist_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wishlist = getItem(position)
        holder.bindData(wishlist)
    }
}