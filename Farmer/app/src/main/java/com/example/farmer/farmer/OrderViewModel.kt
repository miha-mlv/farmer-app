package com.example.farmer.farmer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.customer.data.network.model.OrderStatus
import com.example.farmer.farmer.network.OrderUpdateRequest
import com.example.farmer.farmer.network.RejectionReason
import com.example.farmer.farmer.repository.OrderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OrderViewModel(application: Application, val orderRepository: OrderRepository) :
    AndroidViewModel(application) {

    private val _ordersArr = MutableSharedFlow<List<OrderHistoryResponse>>()
    val orderArr = _ordersArr.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    private val _isSuccess = MutableSharedFlow<Unit>()
    val isSuccess = _isSuccess.asSharedFlow()

    private val tokenManager = TokenManager(application)
    fun getMyOrders() {
        viewModelScope.launch {
            val response = orderRepository.getMyOrders(tokenManager.getToken()!!)
            _ordersArr.emit(response.getOrDefault(emptyList()))
        }
    }

    fun acceptOrder(id: Long){
        viewModelScope.launch {
            val request = OrderUpdateRequest(
                status = OrderStatus.ACCEPTED,
                reason = null,
                comment = null
            )
            val response = orderRepository.updateOrderStatus(id, request)
            if(response.isSuccess){
                getMyOrders()
                //_isSuccess.emit(Unit)
            }
            else{
                _error.emit("Ошибка сервера, повторите позже!")
            }

        }
    }

    fun rejectOrder(id: Long, reason: RejectionReason?, comment: String?){
        viewModelScope.launch {
            val request = OrderUpdateRequest(
                status = OrderStatus.REJECTED,
                reason = reason,
                comment = comment
            )
            val response = orderRepository.updateOrderStatus(id, request)
            if(response.isSuccess){
                getMyOrders()
                _isSuccess.emit(Unit)
            }
            else{
                _error.emit("Ошибка сервера, повторите позже!")
            }
        }
    }

}