package com.aashutosh.desimall_pro.models.makeOrder

data class Shipping(
    val address_1: String,
    val address_2: String,
    val city: String,
    val country: String,
    val first_name: String,
    val last_name: String,
    val postcode: String,
    val state: String
)