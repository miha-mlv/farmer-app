package com.example.farmer.entity

enum class OrderStatus(val displayName: String, val colorHex: String) {
    PENDING("В ожидании", "#FFA500"), // Оранжевый
    ACCEPTED("Принят", "#4CAF50"),               // Зеленый
    REJECTED("Отклонен", "#F44336"),             // Красный
    COMPLETED("Завершен", "#2196F3"),            // Синий
    CANCELLED("Отменен покупателем", "#9E9E9E")  // Серый
}