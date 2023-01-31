package com.aashutosh.desimall_pro.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.sqlite.db.SimpleSQLiteQuery
import com.aashutosh.desimall_pro.database.DatabaseHelper
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.facebook.bolts.Task.Companion.delay

class KeyValuePagingSource(
    private val databaseHelper: DatabaseHelper,
    private val key: String,
    private val value: String
) : PagingSource<Int, DesiDataResponseSubListItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DesiDataResponseSubListItem> {
        val page = params.key ?: 0
        return try {
            val query = SimpleSQLiteQuery(
                "SELECT * FROM product WHERE $key = ? LIMIT ${params.loadSize} OFFSET ${(page * params.loadSize)}",
                arrayOf(value.trim())

            )
            val entities = databaseHelper.allProduct()
                .getKeyValueBasedProduct(
                    query = query

                )
            /*  limit = params.loadSize,
                    offset = page * params.loadSize,
                    key = key,
                    value = value*/


            if (page != 0) delay(100)
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )


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