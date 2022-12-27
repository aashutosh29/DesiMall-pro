package com.aashutosh.desimall_pro.database

import androidx.room.*
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.DeliveryDetails

@Dao
interface CartDao {

    @Insert
    suspend fun addProduct(cartProduct: CartProduct): Long

    @Query("SELECT * FROM cart")
    suspend fun getCartProduct(): List<CartProduct>

    @Delete
    suspend fun deleteCartProduct(cartProduct: CartProduct): Int

    @Update
    suspend fun updateQuantity(cartItem: CartProduct): Int

    /*delivery*/
    @Insert
    suspend fun addDetails(deliveryDetails: DeliveryDetails): Long

    @Query("SELECT * FROM delivery")
    suspend fun getDetails(): List<DeliveryDetails>

    @Update
    suspend fun updateDetails(deliveryDetails: DeliveryDetails): Int

    @Query("DELETE FROM cart")
    fun deleteAllCart()
}