package com.aashutosh.desimall_pro.paging

import android.content.ContentValues.TAG
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aashutosh.desimall_pro.api.CDSService
import com.aashutosh.desimall_pro.models.product.ProductItem

class FilterPagingSource(
    private val cdsService: CDSService,
    private val categoryId: String,
    private val minPrice: String,
    private val maxPrice: String,
    private val orderBy: String,
    private val ascOrDesc: String,
    private val searchKeyword: String
) :
    PagingSource<Int, ProductItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductItem> {
        val position = params.key ?: 1

        val response = cdsService.getSearchProduct(
            position,
            categoryId,
            minPrice,
            maxPrice,
            orderBy.ifEmpty { "date" },
            ascOrDesc.ifEmpty { "asc" },
            searchKeyword
        )

        Log.d(TAG, "total page: ${response.headers().get("X-WP-TotalPages")}")
        return LoadResult.Page(
            data = response.body()!!,
            prevKey = if (position == 1) null else position - 1,
            nextKey = if (position == response.headers().get("X-WP-TotalPages")!!
                    .toInt()
            ) null else position + 1

        )
    }

    override fun getRefreshKey(state: PagingState<Int, ProductItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}