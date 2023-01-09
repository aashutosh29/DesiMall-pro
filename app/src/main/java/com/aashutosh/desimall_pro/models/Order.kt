package com.aashutosh.desimall_pro.models

data class Order(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val zip: String,
    val branchCode: String,
    val date: String,
    val status: String,
    val products: List<String>,
    val totalProduct: String,
    val totalPrice: String


)
