package com.aashutosh.desimall_pro.models

data class NominatimResponse(
    val display_name: String,
    val address: Address
)

data class Address(
    val state_district: String?,
    val state: String?,
    val postcode : String?
    // Add other required fields if needed
)