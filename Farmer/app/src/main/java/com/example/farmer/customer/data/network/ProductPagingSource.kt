package com.example.farmer.customer.data.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.farmer.customer.data.network.model.ProductWithPosDto

class ProductPagingSource(
    private val apiService: CustomerApi,
    private val category: String?,
    private val minPrice: Double?,
    private val maxPrice: Double?
) : PagingSource<Int, ProductWithPosDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductWithPosDto> {
        val position = params.key ?: 0
        return try {
            val response = apiService.getProducts(
                page = position,
                size = params.loadSize,
                category = category,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
            val products = response.content

            LoadResult.Page(
                data = products,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (position >= response.page.totalPages - 1 || products.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            Log.e("PagingSource", "Loading error", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductWithPosDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}