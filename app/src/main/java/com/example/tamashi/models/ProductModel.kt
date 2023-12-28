package com.example.tamashi.models

data class ProductModel(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val thumbnail: String,
    val status: String,
    val po_date: String,
    val id_category: Int
)