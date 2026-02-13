package com.example.farmer.customer.ui.basket

import android.app.Application
import android.view.animation.Transformation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.repository.ProductBasketRepository
import kotlinx.coroutines.launch

class BasketViewModel(
    application: Application,
    private val basketRepository: ProductBasketRepository
) : AndroidViewModel(application) {

    val basketItems = basketRepository.getBasketItems().asLiveData()

    val totalPrice = basketItems.map { items ->
        items.sumOf { it.priceText.dropLast(3).toInt()*it.quantity }
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

}