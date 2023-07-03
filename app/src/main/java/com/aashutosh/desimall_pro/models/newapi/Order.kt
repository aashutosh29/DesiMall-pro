package com.aashutosh.desimall_pro.models.newapi

data class Address(
    val country: String,
    val city: String,
    val pincode: String,
    val deliveredAddress: String,
    val state: String
)

data class Product(
    val product_id: String,
    val name: String,
    val price: Float,
    val quantity: Int,
    val measurement: String,
    val category: String,
    val image: String,
    val stock: Int
)

data class OrderData(
    val orderedStoreName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone_number: String,
    val address: Address,
    val products: List<Product>
)
