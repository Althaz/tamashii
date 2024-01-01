package com.example.tamashi.models

data class OrderModel(
    val id: Int,
    val qty: Int,
    val price: Double,
    val total_price: Double,
    val status: String,
    val name: String,
    val thumbnail: String
)
