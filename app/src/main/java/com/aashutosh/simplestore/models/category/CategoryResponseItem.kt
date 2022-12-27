package com.aashutosh.simplestore.models.category

data class CategoryResponseItem(
    val _links: Links,
    val barcode: String,
    val count: Int,
    val description: String,
    val display: String,
    val id: Int,
    val image: Image,
    val menu_order: Int,
    val name: String,
    val parent: Int,
    val slug: String
)