package com.example.farmer.customer.ui.basket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.network.model.OrderRequest
import com.example.farmer.customer.data.network.model.OrderResponse
import com.example.farmer.customer.data.repository.ProductBasketRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BasketViewModel(
    application: Application,
    private val basketRepository: ProductBasketRepository,
    private val tokenManager: TokenManager
) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()
    private val _resultRequest = MutableSharedFlow<Result<OrderResponse>?>()
    val resultRequest = _resultRequest.asSharedFlow()
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()
    val basketItems = basketRepository.getBasketItems().asLiveData()

    val totalPrice = basketItems.map { items ->
        items.sumOf { it.priceText.dropLast(3).toInt() * it.quantity }
    }

    fun plusQuantity(item: BasketItem) {
        viewModelScope.launch {
            basketRepository.updateQuantity(item.id, item.quantity + 1)
        }
    }

    fun minusQuantity(item: BasketItem) {
        if (item.quantity > 1) {
            viewModelScope.launch {
                basketRepository.updateQuantity(item.id, item.quantity - 1)
            }
        }
    }

    fun removeItem(item: BasketItem) {
        viewModelScope.launch {
            basketRepository.removeFromBasket(item.id)
        }
    }

    fun clearBasket(){
        viewModelScope.launch {
            basketRepository.clearBasket()
        }
    }

    fun sendOrder(products: List<BasketItem>, totalAmount: Int) {
        val request = OrderRequest(
            totalAmount = totalAmount,
            products = products,
            token = tokenManager.getToken()!!
        )

        viewModelScope.launch {
            try{
                _isLoading.value = true
                val result = basketRepository.sendOrder(request)
                _resultRequest.emit(result)
                _isLoading.value = false
            }catch(e: Exception){
                _error.emit("Ошибка сервера")
            }
        }
    }

}






































