package com.example.tamashi.adapters

import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.tamashi.DetailActivity
import com.example.tamashi.PreferencesManager
import com.example.tamashi.R
import com.example.tamashi.models.ProductModel
import org.json.JSONObject

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
        val productItem : CardView = itemView.findViewById(R.id.product_card_item)
        val addWishlistBtn : ImageView = itemView.findViewById(R.id.product_card_add_wishlist_btn)
        fun bindData(product: ProductModel){
            Glide.with(productImage)
                .load(PreferencesManager.URL + product.thumbnail)
                .into(productImage)
            Toast.makeText(itemView.context, product.thumbnail, Toast.LENGTH_LONG)
            productTitle.text = product.name
            productPrice.text = "Rp. " + product.price

            productItem.setOnClickListener{
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("id_produk", product.id.toString())
                itemView.context.startActivity(intent)
            }

            addWishlistBtn.setOnClickListener{
                if (PreferencesManager.JWT == ""){
                    Toast.makeText(itemView.context, "Harap login terlebih dahulu!", Toast.LENGTH_LONG).show();
                } else {
                    addWishlist(product.id, addWishlistBtn)
                }
            }
        }

        private fun addWishlist(id: Int, addWishlistBtn: ImageView) {
            val wishlistObj = JSONObject()
            wishlistObj.put("users_permissions_user", PreferencesManager.ID_USER)
            wishlistObj.put("products", id)
            val dataObj = JSONObject()
            dataObj.put("data", wishlistObj)
            val url = PreferencesManager.URL + "/api/wishlists"

            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, dataObj,
                Response.Listener { response ->
                    val data = response.getJSONObject("data")
                    if(data != null){
                        addWishlistBtn.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple_500),
                            PorterDuff.Mode.MULTIPLY)
                        Toast.makeText(itemView.context, "Berhasil menambah wishlist", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(itemView.context, "Gagal menambah wishlist", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(itemView.context, error.message, Toast.LENGTH_LONG).show()
                }) {
                override fun getBodyContentType(): String {
                    return "application/json"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer " + PreferencesManager.JWT
                    return headers
                }
            }
            val queue = Volley.newRequestQueue(itemView.context)
            queue.add(jsonRequest)
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