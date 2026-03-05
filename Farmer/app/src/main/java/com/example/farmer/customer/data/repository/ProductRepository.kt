package com.example.farmer.customer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.farmer.customer.data.local.dao.BasketDao
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.network.CustomerApi
import com.example.farmer.customer.data.network.ProductPagingSource
import com.example.farmer.customer.data.network.model.Product
import com.example.farmer.customer.data.network.model.ProductWithPosDto
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для работы с товарами.
 * @param apiService интерфейс Retrofit для сетевых запросов.
 */
class ProductRepository(private val apiService: CustomerApi) {

    /**
     * Метод создает поток Flow данных пагинации.
     * Каждый раз, когда вызывается этот метод, создается новый объект Pager.
     * @param category выбранная категория товаров (опционально)
     * @param minPrice минимальная цена (опционально)
     * @param maxPrice максимальная цена (опционально)
     * @return Flow<PagingData<Product>> — поток данных, который будет "слушать" ViewModel.
     */
    fun getProductsStream(
        name: String? = null,
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Flow<PagingData<ProductWithPosDto>> {

        return Pager(
            // Конфигурация пагинации
            config = PagingConfig(
                pageSize = 20,           // Количество товаров, загружаемых за один раз
                enablePlaceholders = false, // Показывать ли "пустые" карточки, пока грузятся данные
                initialLoadSize = 20,    // Размер первой загрузки (обычно равен или чуть больше pageSize)
                prefetchDistance = 5     // За сколько элементов до конца списка начинать подгрузку следующей страницы
            ),
            // Фабрика, которая создает наш PagingSource
            pagingSourceFactory = {
                ProductPagingSource(
                    apiService = apiService,
                    name = name,
                    category = category,
                    minPrice = minPrice,
                    maxPrice = maxPrice
                )
            }
        ).flow // Превращаем всё это в поток данных
    }

    suspend fun getProductImages(productId: Long): List<String> {
        return apiService.productImages(productId)
    }

}