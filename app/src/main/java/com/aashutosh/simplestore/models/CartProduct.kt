package com.aashutosh.simplestore.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartProduct(
    @PrimaryKey(autoGenerate = true)
    val productId: Int,
    val name: String,
    val image: String,
    val details: String,
    var quantity: Int,
    val price: Double,
    val mrp: Double
)
