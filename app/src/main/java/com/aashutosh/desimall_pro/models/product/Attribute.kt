package com.aashutosh.desimall_pro.models.product

data class Attribute(
    val id: Int,
    val name: String,
    val options: List<String>,
    val position: Int,
    val variation: Boolean,
    val visible: Boolean
)