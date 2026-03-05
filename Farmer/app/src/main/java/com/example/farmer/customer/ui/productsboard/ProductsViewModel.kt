package com.example.farmer.customer.ui.productsboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.network.model.Product
import com.example.farmer.customer.data.network.model.toUI
import com.example.farmer.customer.data.repository.ProductBasketRepository
import com.example.farmer.customer.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val application: Application,
    private val repository: ProductRepository,
    private val basketRepository: ProductBasketRepository
) : AndroidViewModel(application) {

    val searchQuery = MutableStateFlow("")
    private val _category = MutableStateFlow<String?>(null)
    private val _minPrice = MutableStateFlow<Double?>(null)
    private val _maxPrice = MutableStateFlow<Double?>(null)


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products = combine(
        searchQuery.debounce(500), // Задержка для поиска
        _category,
        _minPrice,
        _maxPrice
    ) { query, cat, min, max ->
        // Группируем всё в один объект или кортеж
        FilterParams(query, cat, min, max)
    }.flatMapLatest { params ->
        // Каждый раз, когда фильтры меняются, создаем новый поток из репозитория
        repository.getProductsStream(
            name = if (params.query.isBlank()) null else params.query,
            category = params.category,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice
        ).map { pagingData ->
            pagingData.map { dto -> dto.toUI() }
        }
    }.flowOn(Dispatchers.Default).cachedIn(viewModelScope)

    fun setFilters(min: Double?, max: Double?, newCategory: String?) {
        _minPrice.value = min
        _maxPrice.value = max
        _category.value = newCategory
    }

    fun addToBasket(product: Product){
        viewModelScope.launch {
            val basketItem = BasketItem(
                id = product.id,
                name = product.name,
                priceText = product.priceText,
                farmName = product.farmName,
                imageUrl = product.images,
                farmerId = product.farmerId
            )
            basketRepository.addToBasket(basketItem)
        }
    }

    data class FilterParams(
        val query: String,
        val category: String?,
        val minPrice: Double?,
        val maxPrice: Double?
    )

}