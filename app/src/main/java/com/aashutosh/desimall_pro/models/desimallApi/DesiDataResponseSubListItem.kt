package com.aashutosh.desimall_pro.models.desimallApi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class DesiDataResponseSubListItem(
    @PrimaryKey
    val sku: String,
    val Published: String,
    val brand_name: String,
    val category_name: String,
    val color_name: String,
    val company_name: String,
    val product_group_name: String,
    val product_quantity: Double,
    val size_name: String,
    val sku_description: String,
    val sku_name: String,
    val subcategory_name: String,
    val tax_category: String,
    val uom: String,
    val variant_cost_price: Double,
    val variant_mrp: Double,
    val variant_sale_price: Double,
    val weight_name: String,
    val sku_barcode: String
)