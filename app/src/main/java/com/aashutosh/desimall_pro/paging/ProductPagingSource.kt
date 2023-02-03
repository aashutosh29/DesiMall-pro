package com.aashutosh.desimall_pro.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aashutosh.desimall_pro.database.DatabaseHelper
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.facebook.bolts.Task.Companion.delay

class ProductPagingSource(
    private val databaseHelper: DatabaseHelper,
    private val value: String,
    private val isSearch: Boolean
) : PagingSource<Int, DesiDataResponseSubListItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DesiDataResponseSubListItem> {
        val page = params.key ?: 0
        return try {
            if (isSearch) {
                if (value == "1") {
                    val entities =
                        databaseHelper.allProduct()
                            .getPagedList(params.loadSize, page * params.loadSize)
                    if (page != 0) delay(100)
                    LoadResult.Page(
                        data = entities,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (entities.isEmpty()) null else page + 1
                    )
                } else {


                    val entities = databaseHelper.allProduct()
                        .getSearchedProduct(
                            params.loadSize,
                            page * params.loadSize,
                            value.uppercase().trim()
                        )
                    if (page != 0) delay(100)
                    LoadResult.Page(
                        data = entities,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (entities.isEmpty()) null else page + 1
                    )
                }

            } else {
                if (value.length == 1) {
                    val entities = databaseHelper.allProduct()
                        .getAlphaProduct(params.loadSize, page * params.loadSize, value)
                    if (page != 0) delay(100)
                    LoadResult.Page(
                        data = entities,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (entities.isEmpty()) null else page + 1
                    )
                } else {
                    val entities = databaseHelper.allProduct()
                        .getCategoryBasedProduct(params.loadSize, page * params.loadSize, value)
                    if (page != 0) delay(100)
                    LoadResult.Page(
                        data = entities,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (entities.isEmpty()) null else page + 1
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DesiDataResponseSubListItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}