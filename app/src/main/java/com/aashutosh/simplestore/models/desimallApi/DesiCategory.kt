package com.aashutosh.simplestore.models.desimallApi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class DesiCategory(
    @PrimaryKey
    val name: String
)
