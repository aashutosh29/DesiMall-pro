package com.aashutosh.desimall_pro.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.aashutosh.desimall_pro.models.desimallApi.DesiCategory
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem

@Dao
interface ProductDao {

    @Transaction
    suspend fun addDesiCategory(cat: List<DesiCategory>): Boolean {
        if (getCategoryCount() >= 1) {
            if (deleteCat() > 1) {
                cat.forEach { insertCategory(it) }
                return true
            }
        } else {
            cat.forEach { insertCategory(it) }
            return true
        }
        return false
    }


    @Transaction
    suspend fun addDesiProduct(objects: List<DesiDataResponseSubListItem>): Boolean {
        return try {
            if (getProductCount() >= 1) {
                if (deleteAll() > 1) {
                    objects.forEach { insert(it) }
                    true
                } else {
                    false
                }
            } else {
                objects.forEach { insert(it) }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: DesiDataResponseSubListItem)

    @Query("SELECT * FROM product  LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(limit: Int, offset: Int): List<DesiDataResponseSubListItem>

    @Query("SELECT * FROM product where subcategory_name=:categoryName  LIMIT :limit OFFSET :offset")
    suspend fun getCategoryBasedProduct(
        limit: Int, offset: Int, categoryName: String
    ): List<DesiDataResponseSubListItem>

    /* @Query("SELECT * FROM product where :key=:value  LIMIT :limit OFFSET :offset")
     suspend fun getKeyValueBasedProduct(
         limit: Int, offset: Int, key: String, value: String
     ): List<DesiDataResponseSubListItem>*/

    @RawQuery
    suspend fun getKeyValueBasedProduct(query: SupportSQLiteQuery): List<DesiDataResponseSubListItem>


    @Query("SELECT * FROM product ")
    suspend fun getAllList(): List<DesiDataResponseSubListItem>

    @Query("DELETE FROM product")
    fun deleteAll(): Int

    @Query("DELETE FROM category")
    fun deleteCat(): Int

    @Query("SELECT * FROM product where sku_name LIKE :alpha || '%'  LIMIT :limit OFFSET :offset")
    suspend fun getAlphaProduct(
        limit: Int, offset: Int, alpha: String
    ): List<DesiDataResponseSubListItem>

    @Query("SELECT * FROM product ")
    fun getAllListLiveData(): LiveData<List<DesiDataResponseSubListItem>>
    @Query("SELECT * FROM product where sku_name LIKE '%' || :search || '%'  LIMIT :limit OFFSET :offset")
    suspend fun getSearchedProduct(
        limit: Int, offset: Int, search: String
    ): List<DesiDataResponseSubListItem>

    @Query("SElECT * FROM category")
    suspend fun getAllCategory(): List<DesiCategory>

    @Query("SELECT COUNT(*) FROM product")
    suspend fun getProductCount(): Int

    @Query("SELECT COUNT(*) FROM category")
    suspend fun getCategoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(cat: DesiCategory)

    @Query("SELECT * FROM product WHERE sku_barcode=:barCode")
    suspend fun getBarCode(barCode: String): List<DesiDataResponseSubListItem>

    @Query("SELECT * FROM product WHERE sku=:sku")
    suspend fun getProductBySku(sku: String): List<DesiDataResponseSubListItem>

    @Query("SELECT * FROM product WHERE sku=:productId")
    suspend fun getProductDetails(productId: Int): DesiDataResponseSubListItem?


}