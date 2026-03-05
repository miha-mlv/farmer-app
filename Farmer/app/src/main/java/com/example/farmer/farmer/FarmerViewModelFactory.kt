package com.example.farmer.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FarmerViewModelFactory(private val creators: Map<Class<out ViewModel>, () -> ViewModel>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull(){
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")

        try {
            @Suppress("UNCHECKED_CAST")
            return creator.invoke() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}