package com.aashutosh.desimall_pro.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.aashutosh.desimall_pro.api.CDSService
import com.aashutosh.desimall_pro.database.DatabaseHelper
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.DeliveryDetails
import com.aashutosh.desimall_pro.models.category.CategoryResponse
import com.aashutosh.desimall_pro.models.desimallApi.DesiCategory
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.models.product.Products
import com.aashutosh.desimall_pro.paging.ProductPagingSource
import com.aashutosh.desimall_pro.sealed.StateResponse
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val cdsService: CDSService,
    private val databaseHelper: DatabaseHelper,
) {
    private val categoryLiveData = MutableLiveData<StateResponse<CategoryResponse>>()
    private val productListLiveData = MutableLiveData<Products>()

    private val productDetailsLiveData = MutableLiveData<DesiDataResponseSubListItem?>()
    private val cartSizeLiveData = MutableLiveData<Int>()
    private val bannerLiveData = MutableLiveData<List<String>>()

    private val categoryItemLiveData = MutableLiveData<List<String>>()


    val categoryResponse: LiveData<StateResponse<CategoryResponse>>
        get() = categoryLiveData


    val categoryItem: MutableLiveData<List<String>>
        get() = categoryItemLiveData

    val bannerResponse: LiveData<List<String>>
        get() = bannerLiveData
    val productsResponse: LiveData<Products>
        get() = productListLiveData


    val cartSize: LiveData<Int>
        get() = cartSizeLiveData
    val productDetailsResponse: MutableLiveData<DesiDataResponseSubListItem?>
        get() = productDetailsLiveData

    suspend fun getCategory(page: Int) {
        try {
            categoryLiveData.postValue(StateResponse.Loading())
            val result = cdsService.getCategory(1)
            if (result.body() != null) {
                categoryLiveData.postValue(StateResponse.Success(result.body()))
            } else {
                categoryLiveData.postValue(StateResponse.Error("Error in api"))
            }
        } catch (e: Exception) {
            categoryLiveData.postValue(StateResponse.Error(e.toString()))
        }
    }

    fun getPagingProduct(productId: Int) =
        Pager(config = PagingConfig(pageSize = 10, maxSize = 100), pagingSourceFactory = {
            ProductPagingSource(
                databaseHelper, productId.toString(), true
            )
        }).liveData


    fun getDesiSearchProduct(
        keyword: String
    ) = Pager(config = PagingConfig(pageSize = 10, maxSize = 100), pagingSourceFactory = {
        ProductPagingSource(databaseHelper, keyword, true)
    }).liveData

    fun getDesiCatProduct(
        cat: String
    ) = Pager(config = PagingConfig(pageSize = 10, maxSize = 100), pagingSourceFactory = {
        ProductPagingSource(databaseHelper, cat, false)
    }).liveData


    suspend fun get5Product(catId: Int) {
        val result = cdsService.getCategoryBasedProduct5(page = 15, category = catId)
        productListLiveData.postValue(result.body())
    }

    suspend fun getProductDetails(productId: Int) {
        try {
            val result = databaseHelper.allProduct().getProductDetails(productId)
            productDetailsLiveData.postValue(result)
        } catch (e: Exception) {
            Log.d(TAG, "getProductDetails: ")
        }
    }

    suspend fun getAllList(isFirst: Boolean) {
        if (isFirst) {
            val productList = databaseHelper.allProduct().getAllList()
            val catList: ArrayList<String> = ArrayList()
            for (product in productList) {
                catList.add(product.subcategory_name)
            }
            val catE: ArrayList<DesiCategory> = ArrayList()
            val catC = catList.distinct()
            for (cat in catC) {
                catE.add(DesiCategory(cat))
            }
            databaseHelper.allProduct().addDesiCategory(catE)
            //categoryItem.postValue(catList.distinct() as ArrayList<String>)
        }
    }

    suspend fun addCat(cat: List<DesiCategory>) {
        databaseHelper.allProduct().addDesiCategory(cat)
    }

    suspend fun desiProduct(branchCode: Int, first: Boolean) {
        try {
            val result = cdsService.getDesiProduct("DSM", branchCode.toString())
            databaseHelper.allProduct().addDesiProduct(result[0])
        } catch (e: Exception) {
            Log.d(TAG, "getDesiProduct: ")
        }


    }

    suspend fun allCat(): List<DesiCategory> {

        return databaseHelper.allProduct().getAllCategory();
    }

    suspend fun addProductToCart(cartProduct: CartProduct): Long {
        return try {
            return databaseHelper.cartDao().addProduct(cartProduct)

        } catch (_: Exception) {
            (Log.d(TAG, "addProductToCart: ")).toLong()
        }
    }

    suspend fun createDelivery(deliveryDetails: DeliveryDetails): Long {
        return try {
            return databaseHelper.cartDao().addDetails(deliveryDetails)
        } catch (e: Exception) {
            Log.d(TAG, "updateDelivery: $e")
        }.toLong()
    }


    suspend fun getCartCount() {
        try {
            val result = databaseHelper.cartDao().getCartProduct().size
            cartSizeLiveData.postValue(result)

        } catch (e: Exception) {
            Log.d(TAG, "getCartCount: ")
        }
    }

    suspend fun getBarCodeProduct(barCode: String) {
        try {
            val result = databaseHelper.allProduct().getBarCode(barCode)

            productDetailsLiveData.postValue(result[0])
        } catch (e: Exception) {
            Log.d(TAG, "getProductDetails: $e")
        }


    }

    suspend fun getDummyCart(): List<CartProduct> {
        return databaseHelper.cartDao().getCartProduct()
    }

    suspend fun deleteCartProduct(cartProduct: CartProduct): Int {
        return databaseHelper.cartDao().deleteCartProduct(cartProduct)

    }

    suspend fun updateQty(cartItem: CartProduct): Int {
        return databaseHelper.cartDao().updateQuantity(cartItem)

    }

    fun getSliderImage() {
        val imageList: MutableList<String> = ArrayList()
        val firebaseFirestone = Firebase.firestore
        firebaseFirestone.firestoreSettings
        firebaseFirestone.collection("banner").get().addOnSuccessListener { result ->
            for (document in result) {
                imageList.add(document.data["image"].toString())
                Log.d(TAG, " ${document.id} => ${document.data["image"]}")
            }
            bannerLiveData.postValue(imageList)
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents.", exception)

        }
    }


    suspend fun addDeliveryAddress(deliveryDetails: DeliveryDetails): Long {
        return databaseHelper.cartDao().addDetails(deliveryDetails)

    }

    suspend fun updateDelivery(deliveryDetails: DeliveryDetails): Int {
        return try {
            return databaseHelper.cartDao().updateDetails(deliveryDetails)
        } catch (e: Exception) {
            Log.d(TAG, "updateDelivery: $e")
        }

    }


    suspend fun getDelivery(): List<DeliveryDetails> {
        return databaseHelper.cartDao().getDetails()
    }

    suspend fun createOrder(requestBody: RequestBody): retrofit2.Response<Any> {
        return cdsService.createOrder(requestBody)
    }

    fun deleteALlCart() {
        return databaseHelper.cartDao().deleteAllCart()
    }


    suspend fun sendNotification(
        topic: Any,
        name: String,
        address: String,
        totalProduct: String
    ): ResponseBody {
        val notification =
            """{"to":"/topics/order_app","priority": "high","data": {"name": "$name" ,"address" : "$address","totalProduct":"$totalProduct",}}"""
        val fcmBody = RequestBody.create(MediaType.parse("application/json"), notification)
        return cdsService.pushNotification(
            "https://fcm.googleapis.com/fcm/send",
            "key= AAAAsk6J7JA:APA91bFx7s4Lj7sVBtD4OUnT-wrfudt8bdxGHnDcCJs4dm2uRRH76eYXIWch_wp-w6tNJ7FsvIZaSfQvb-uQzEOQXAzEVZuYHuYSbgXn2TVtnOnL2loLWSOBherWNpwF7FZmdYeg-lwk",
            "application/json",
            fcmBody
        )

    }


}