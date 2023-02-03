package com.aashutosh.desimall_pro.viewModels


import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.DeliveryDetails
import com.aashutosh.desimall_pro.models.category.CategoryResponse
import com.aashutosh.desimall_pro.models.desimallApi.DesiCategory
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.models.makeOrder.OrderPlace
import com.aashutosh.desimall_pro.models.product.Products
import com.aashutosh.desimall_pro.repository.ProductRepository
import com.aashutosh.desimall_pro.sealed.StateResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {

    init {
        viewModelScope.launch {
        }
    }

    val category: LiveData<StateResponse<CategoryResponse>>
        get() = repository.categoryResponse

    val categoryItem: LiveData<List<String>>
        get() = repository.categoryItem

    val productItem: MutableLiveData<DesiDataResponseSubListItem?>
        get() = repository.productDetailsResponse

    val cartSize: LiveData<Int>
        get() = repository.cartSize

    val bannerList: LiveData<List<String>>
        get() = repository.bannerResponse

    val product5: LiveData<Products>
        get() = repository.productsResponse

    suspend fun fetchProductItem(productId: Int) {
        repository.getProductDetails(productId);
    }

    suspend fun getDesiProduct(branchCode: Int): Boolean {
        return repository.desiProduct(branchCode)
    }


    suspend fun allCategory(): List<DesiCategory> {
        return repository.allCat()
    }


    fun desiPagingProduct(productId: Int): LiveData<PagingData<DesiDataResponseSubListItem>> {
        return repository.getPagingProduct(productId).cachedIn(viewModelScope)
    }


    fun getCatBasedDesiProduct(value: String): LiveData<PagingData<DesiDataResponseSubListItem>> {

        return repository.getDesiCatProduct(value).cachedIn(viewModelScope)
    }

    fun getKeyValueBasedProduct(
        query: String
    ): LiveData<PagingData<DesiDataResponseSubListItem>> {
        return repository.getKeyValueProduct(query = query).cachedIn(viewModelScope)
    }


    fun getDesiSearch(keyword: String): LiveData<PagingData<DesiDataResponseSubListItem>> {

        return repository.getDesiSearchProduct(keyword).cachedIn(viewModelScope)
    }


    suspend fun insertToCart(cartProduct: CartProduct): Long {
        return repository.addProductToCart(cartProduct)
    }

    suspend fun createDelivery(deliveryDetails: DeliveryDetails): Long {
        return repository.createDelivery(deliveryDetails)
    }

    suspend fun getCartSize() {
        return repository.getCartCount()
    }

    suspend fun getFilteredCategoryFromProduct(): Boolean {
        return repository.filterCategoryFormProduct()
    }

    suspend fun getBarCodeBasedItem(barcode: String) {
        repository.getBarCodeProduct(barcode);
    }

    suspend fun getIdBasedProduct(sku: String): DesiDataResponseSubListItem {
        return repository.getProductById(sku)
    }


    suspend fun deleteProduct(cartProduct: CartProduct): Int {
        return repository.deleteCartProduct(cartProduct)

    }

    suspend fun updateDelivery(deliveryDetails: DeliveryDetails): Int {
        return repository.updateDelivery(deliveryDetails)
    }


    suspend fun updateQty(cartItem: CartProduct): Int {
        return repository.updateQty(cartItem)

    }


    suspend fun getDummyCart(): List<CartProduct> {
        return repository.getDummyCart()
    }

    suspend fun getProfileDetails(): List<DeliveryDetails> {
        return repository.getDelivery()
    }

    fun getBannerList() {
        return repository.getSliderImage()
    }

    suspend fun createOrder(order: OrderPlace): retrofit2.Response<Any> {
        val gson = Gson()
        Log.d(TAG, "createOrder: " + gson.toJson(order).toString())

        val request = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            gson.toJson(order).toString()
        )

        return repository.createOrder(request)
    }

    fun deleteAllCart() {
        return repository.deleteALlCart()
    }

    suspend fun sendNotification(
        topic: String,
        name: String,
        address: String,
        totalProduct: String
    ): ResponseBody {
        return repository.sendNotification(
            topic = topic,
            name = name,
            address = address,
            totalProduct = totalProduct
        )
    }

}