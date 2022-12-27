package com.aashutosh.simplestore.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery")
data class DeliveryDetails(
    @PrimaryKey
     val id: Int,
     val name: String,
     val mobileNum: String,
     val address: String,
     val landMark: String,
     val zipCode: String
)
