package com.example.farmer.farmer

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.PixelCopy
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.farmer.common.network.AuthApi
import com.example.farmer.common.network.FarmerProfile
import com.example.farmer.common.util.TokenManager
import com.example.farmer.farmer.network.FarmerApi
import com.example.farmer.farmer.network.PointOfSale
import com.example.farmer.farmer.network.PointOfSaleRequest
import com.example.farmer.farmer.network.PosStatusRequest
import com.example.farmer.farmer.network.Product
import com.example.farmer.farmer.network.ProductRequest
import com.example.farmer.farmer.network.RetrofitClientFarmer
import com.example.farmer.farmer.network.TokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class FarmerViewModel(private val api: FarmerApi, application: Application) : ViewModel() {

    private val tokenManager = TokenManager(application)
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()
    private val _isSuccess = MutableSharedFlow<Unit>()
    val isSuccess = _isSuccess.asSharedFlow()

    var isSaleStarted: Boolean = false
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()
    private val _profileData = MutableSharedFlow<FarmerProfile>()
    val profileData = _profileData.asSharedFlow()
    private val _pointsOfSale = MutableStateFlow<List<PointOfSale>>(emptyList())
    val pointOfSale = _pointsOfSale.asStateFlow()

    fun saveProduct(productData: RequestBody, imageParts: List<MultipartBody.Part>) {
        viewModelScope.launch (Dispatchers.IO){
            withContext(Dispatchers.Main){
                _isLoading.value = true
            }
            try {
                val response = api.createProduct(productData = productData, images = imageParts)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main){
                        _isSuccess.emit(Unit)
                    }
                } else {
                    withContext(Dispatchers.Main){
                        _error.emit("Ошибка: ${response.message()}(${response.code()})")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    _error.emit("Проверьте интернет-соединение: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main){
                    _isLoading.value = false
                }
            }
        }
    }

    fun loadMyProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("loadMyProducts()", "Запрос отправлен")
                val token = tokenManager.getToken().toString()
                Log.d("loadMyProducts: TOKEN", token)
                val response = api.getMyProducts(token)
                Log.d("loadMyProducts()", "результат: ${response.body()!!.size}")
                if (response.isSuccessful) {
                    _products.value = response.body() ?: emptyList()
                } else {
                    _error.emit("Ошибка загрузки товара")
                }
            } catch (e: Exception) {
                _error.emit("Ошибка сети при загрузке товара: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Вспомогательная функция для подготовки части изображения
    private fun prepareImagePart(uri: Uri, context: Context): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile =
            File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg") // Уникальное имя
        FileOutputStream(tempFile).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        // Оптимизация: сжатие изображения (Bitmap.compress)
        // Если хотите сжимать, то здесь нужно декодировать inputStream в Bitmap,
        // сжать его, а затем записать в tempFile.
        // Пока оставляем простое копирование для ясности.
        // val bitmap = BitmapFactory.decodeStream(inputStream)
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 80, FileOutputStream(tempFile))

        val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", tempFile.name, requestFile)
    }

    fun getProfile() {
        viewModelScope.launch {
            try {
                val response = api.getProfile(tokenManager.getToken().toString())
                if (response.isSuccessful) {
                    _profileData.emit(response.body() ?: throw Exception("null pointer response"))
                } else {
                    _error.emit("Ошибка загрузка профиля")
                }
            } catch (e: Exception) {
                _error.emit("Ошибка сети при загрузке профиля")
            }
        }
    }

    fun addPoint(namePoint: String, addressPoint: String, latPoint: Double, lonPoint: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = PointOfSaleRequest(
                    name = namePoint,
                    address = addressPoint,
                    latitude = latPoint,
                    longitude = lonPoint
                )
                Log.d("token:", tokenManager.getToken()!!)
                val response = api.savePoint(request, tokenManager.getToken()!!)
                if (response.isSuccessful) {
                    _isSuccess.emit(Unit)
                } else {
                    _error.emit("Ошибка добавления точки про")
                }
            } catch (e: Exception) {
                _error.emit("Ошибка сети при сохранении точки продажи")
            } finally {
                _isLoading.value = false
            }
        }

    }

    fun deletePoint(id: Long) {
        val token = tokenManager.getToken()!!
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = api.deletePoint(id, token)
                if (response.isSuccessful) {
                    _isSuccess.emit(Unit)
                } else {
                    _error.emit("Ошибка удаления точки продажи")
                }
            } catch (e: Exception) {
                _error.emit("Ошибка: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPoints() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken().toString()
                Log.d("token getPoints", token)
                val response = api.getMyPoint(token)
                if (response.isSuccessful) {
                    _pointsOfSale.value = response.body() ?: emptyList()
                    _isSuccess.emit(Unit)
                }
            } catch (e: Exception) {
                _error.emit("Ошибка: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun activatePOS(posId: Long, productIds: List<Long>, isActive: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken().toString()
                Log.d("activate pos", token)
                val request = PosStatusRequest(isActive, productIds)
                val response = api.updatePosStatus(posId, request, token)
                if(response.isSuccessful){
                    _isSuccess.emit(Unit)
                }else{
                    _error.emit("неизвестная ошибка")
                }
            }catch(e: Exception){
                _error.emit("Ошибка: ${e.message}")
                Log.d("Error posActivate: ", "${e.message}")
            }finally {
                _isLoading.value = false
            }
        }
    }
}