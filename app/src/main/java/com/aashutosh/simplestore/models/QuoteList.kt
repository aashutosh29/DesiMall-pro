package com.aashutosh.simplestore.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test")
data class QuoteList(
    @PrimaryKey(autoGenerate = true)
    val count: Int,
    val lastItemIndex: Int,
    val page: Int,
    val totalCount: Int,
    val totalPages: Int
)