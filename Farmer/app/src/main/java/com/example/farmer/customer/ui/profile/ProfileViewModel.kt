package com.example.farmer.customer.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.data.network.model.CustomerProfileResponse
import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.customer.data.repository.ProfileCustomerRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application,
    private val profileCustomerRepository: ProfileCustomerRepository,
    private val tokenManager: TokenManager
) : AndroidViewModel(application = application) {

    private val _profile = MutableSharedFlow<CustomerProfileResponse?>()
    val profile = _profile.asSharedFlow()

    private val _ordersArr = MutableSharedFlow<List<OrderHistoryResponse>>()
    val orderArr = _ordersArr.asSharedFlow()


    private val _logout = MutableSharedFlow<Unit>()
    val logout = _logout.asSharedFlow()

    fun getProfile() {
        viewModelScope.launch {
            try {
                val response = profileCustomerRepository.getProfile(tokenManager.getToken()!!)
                if (response.isSuccess) {
                    _profile.emit(response.getOrNull())
                }
            } catch (e: Exception) {
                throw Exception("Ошибка загрузки профиля")
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            tokenManager.logout()
            _logout.emit(Unit)
        }
    }

    fun getMyOrders() {
        viewModelScope.launch {
            val response = profileCustomerRepository.getMyOrders(tokenManager.getToken()!!)
            _ordersArr.emit(response.getOrDefault(emptyList()))
        }
    }
}