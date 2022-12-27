package com.aashutosh.simplestore.ui

import com.aashutosh.simplestore.models.CartProduct


interface CartInterface {
    suspend fun deleteProduct(cartProduct: CartProduct)
    suspend fun updateQty(cartItem: CartProduct, quantity: Int)
}