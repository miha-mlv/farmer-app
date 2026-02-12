package com.example.farmer.customer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.farmer.customer.data.repository.ProductBasketRepository

class MainCustomerViewModel(
    private val application: Application,
    private val basketRepository: ProductBasketRepository
) : AndroidViewModel(application) {
        val basketCount: LiveData<Int> = basketRepository.getBasketCount().asLiveData()
}