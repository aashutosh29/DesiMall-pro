package com.aashutosh.simplestore.models.makeOrder

data class Order(
    val billing: Billing,
    val line_items: List<LineItem>,
    val payment_method: String,
    val payment_method_title: String,
    val set_paid: Boolean,
    val shipping: Shipping
)