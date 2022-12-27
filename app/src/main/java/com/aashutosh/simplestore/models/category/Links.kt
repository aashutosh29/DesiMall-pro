package com.aashutosh.simplestore.models.category

data class Links(
    val collection: List<Collection>,
    val self: List<Self>,
    val up: List<Up>
)