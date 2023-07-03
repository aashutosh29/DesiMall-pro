package com.aashutosh.desimall_pro.models.newapi



data class Order(
    val address: Address,
    val _id: String,
    val order_id: String,
    val orderedStoreName: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone_number: String,
    val ordered_at: String,
    val status: String,
    val products: List<Product>,
    val total: Int,
    val userRegisterCode: String,
    val orderDate: String,
    val deliveryCharge: Int,
    val grandTotal: Int,
    val __v: Int
)

data class GroupedOrders(
    val orders: List<Order>
)

data class OrderResponse(
    val groupedOrders: Map<String, List<Order>>
)
