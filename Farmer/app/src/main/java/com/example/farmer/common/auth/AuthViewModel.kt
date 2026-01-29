package com.example.farmer.common.auth

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.farmer.common.network.AuthApi
import com.example.farmer.common.network.LoginRequest
import com.example.farmer.common.network.RegisterRequest
import com.example.farmer.common.network.RetrofitClient
import com.example.farmer.common.network.VerificationRequest
import com.example.farmer.common.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val api: AuthApi, application: Application) : ViewModel() {

    private val tokenManager = TokenManager(application)

    sealed class LoginState {
        object Default: LoginState()
        object Loading: LoginState()
        data class Success(val role: String): LoginState()
        data class Error(val message: String): LoginState()
    }

    private val _state = MutableStateFlow<LoginState>(LoginState.Default)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    val loginResult = MutableLiveData<Boolean>()
    val tokenFromServer = MutableLiveData<String>()
    val registrationResult = MutableLiveData<Boolean>()
    val verificationResult = MutableLiveData<Boolean>()

    fun register(
        email: String,
        username: String,
        password: String,
        role: String,
        farmName: String? = null
    ) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val request = RegisterRequest(
                    email = email,
                    username = username,
                    farmName = farmName,
                    role = role,
                    password = password
                )
                val response = api.register(request)
                registrationResult.postValue(response.result)
            }catch (e: Exception) {

            }
        }
    }

    fun verification(code: String, email: String) {
        viewModelScope.launch {
            val request = VerificationRequest(code, email)
            val response = api.verification(request)
            verificationResult.postValue(response.result)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = api.login(request)
                if(response.result){
                    tokenManager.saveToken(response.token)
                    tokenManager.saveRole(response.role)
                    _state.value = LoginState.Success(response.role)
                }else{
                    _state.value = LoginState.Error("Неверная почта или пароль")
                }
            }catch (e: Exception){
                _state.value = LoginState.Error("Проблемы с соединением")
            }
        }
    }

}

class AuthViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(RetrofitClient.instance, application = application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}