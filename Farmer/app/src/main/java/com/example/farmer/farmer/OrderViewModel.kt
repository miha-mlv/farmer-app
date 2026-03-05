package com.example.farmer.farmer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.farmer.repository.OrderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OrderViewModel(application: Application, val orderRepository: OrderRepository) :
    AndroidViewModel(application) {

    private val _ordersArr = MutableSharedFlow<List<OrderHistoryResponse>>()
    val orderArr = _ordersArr.asSharedFlow()

    private val tokenManager = TokenManager(application)
    fun getMyOrders() {
        viewModelScope.launch {
            val response = orderRepository.getMyOrders(tokenManager.getToken()!!)
            _ordersArr.emit(response.getOrDefault(emptyList()))
        }
    }

}