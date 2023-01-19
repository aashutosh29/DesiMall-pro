package com.aashutosh.desimall_pro.database

import androidx.room.*
import com.aashutosh.desimall_pro.models.desimallApi.DesiCategory
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem

@Dao
interface ProductDao {


    @Transaction
    suspend fun addDesiProduct(objects: List<DesiDataResponseSubListItem>) {
        deleteAll()

        objects.forEach { insert(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: DesiDataResponseSubListItem)

    @Query("SELECT * FROM product  LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(limit: Int, offset: Int): List<DesiDataResponseSubListItem>

    @Query("SELECT * FROM product where subcategory_name=:categoryName  LIMIT :limit OFFSET :offset")
    suspend fun getCategoryBasedProduct(
        limit: Int, offset: Int, categoryName: String
    ): List<DesiDataResponseSubListItem>

    @Query("SELECT * FROM product ")
    suspend fun getAllList(): List<DesiDataResponseSubListItem>

    @Query("DELETE FROM product")
    fun deleteAll()

    @Query("DELETE FROM category")
    fun deleteCat()

    @Query("SELECT * FROM product where sku_name LIKE :alpha || '%'  LIMIT :limit OFFSET :offset")
    suspend fun getAlphaProduct(
        limit: Int, offset: Int, alpha: String
    ): List<DesiDataResponseSubListItem>


    @Query("SELECT * FROM product where sku_name LIKE :search || '%'  LIMIT :limit OFFSET :offset")
    suspend fun getSearchedProduct(
        limit: Int, offset: Int, search: String
    ): List<DesiDataResponseSubListItem>

    @Query("SElECT * FROM category")
    suspend fun getAllCategory(): List<DesiCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(cat: DesiCategory)

    @Transaction
    suspend fun addDesiCategory(cat: List<DesiCategory>) {
        deleteCat()
        cat.forEach { insertCategory(it) }
    }

    @Query("SELECT * FROM product WHERE sku_barcode=:barCode")
    suspend fun getBarCode(barCode: String): List<DesiDataResponseSubListItem>

    @Query("SELECT * FROM product WHERE sku=:productId")
    suspend fun getProductDetails(productId: Int): DesiDataResponseSubListItem?

//8906079510094
}