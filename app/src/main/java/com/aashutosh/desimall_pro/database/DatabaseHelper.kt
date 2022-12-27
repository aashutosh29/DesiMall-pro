package com.aashutosh.desimall_pro.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.DeliveryDetails
import com.aashutosh.desimall_pro.models.desimallApi.DesiCategory
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem

@Database(
    entities = [CartProduct::class, DeliveryDetails::class, DesiDataResponseSubListItem::class, DesiCategory::class],
    version = 1
)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun allProduct(): ProductDao

}