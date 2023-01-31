package com.aashutosh.desimall_pro.ui.categoryWithItsProduct

import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem

interface CategoryBasedProductView {
    fun getItemClicked(productItem: DesiDataResponseSubListItem)
    suspend fun addToCart(productItem: DesiDataResponseSubListItem,quantity:Int)
}