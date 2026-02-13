package com.example.farmer.customer.ui.productsboard.productdetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.network.model.Product
import com.example.farmer.customer.data.repository.ProductBasketRepository
import com.example.farmer.customer.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelProductDetail(
    private val application: Application,
    private val repository: ProductRepository,
    private val basketRepository: ProductBasketRepository
) : AndroidViewModel(application) {

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images


    fun loadImages(productId: Long) {
        viewModelScope.launch {
            try {
                _images.value = repository.getProductImages(productId)
                Log.d("loadImages", "images: ${_images.value}")
            } catch (e: Exception) {
                Log.d("loadImages", "${e.message}")
            }
        }
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
}