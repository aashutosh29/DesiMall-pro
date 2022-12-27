package com.aashutosh.desimall_pro.ui

import com.aashutosh.desimall_pro.models.CartProduct


interface CartInterface {
    suspend fun deleteProduct(cartProduct: CartProduct)
    suspend fun updateQty(cartItem: CartProduct, quantity: Int)
}