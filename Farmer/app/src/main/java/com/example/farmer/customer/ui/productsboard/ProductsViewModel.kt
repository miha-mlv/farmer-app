package com.example.farmer.customer.ui.productsboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.farmer.customer.data.network.model.toUI
import com.example.farmer.customer.data.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ProductsViewModel(
    private val application: Application,
    private val repository: ProductRepository
) : AndroidViewModel(application) {
    private val _category = MutableStateFlow<String?>(null)
//    private val tokenManager = TokenManager(application)

    @OptIn(ExperimentalCoroutinesApi::class)
    val products = repository.getProductsStream()
        .map { pagingData ->
            pagingData.map { dto ->
                dto.toUI()
            }
        }.cachedIn(viewModelScope)

    // Метод, который вызовет загрузку/обновление
    fun setCategory(newCategory: String?) {
        _category.value = newCategory
    }

}