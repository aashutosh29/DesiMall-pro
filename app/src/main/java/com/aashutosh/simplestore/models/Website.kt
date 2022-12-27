package com.aashutosh.simplestore.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "website")
data class Website(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imageUrl: String,
    val webUrl: String
)
