package com.aashutosh.simplestore.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aashutosh.simplestore.models.CartProduct
import com.aashutosh.simplestore.models.DeliveryDetails
import com.aashutosh.simplestore.models.desimallApi.DesiCategory
import com.aashutosh.simplestore.models.desimallApi.DesiDataResponseSubListItem

@Database(
    entities = [CartProduct::class, DeliveryDetails::class, DesiDataResponseSubListItem::class, DesiCategory::class],
    version = 1
)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun allProduct(): ProductDao

}