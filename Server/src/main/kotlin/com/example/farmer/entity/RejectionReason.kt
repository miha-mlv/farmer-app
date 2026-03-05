package com.example.farmer.entity

enum class RejectionReason(val text: String) {
    OUT_OF_STOCK("Товара нет в наличии"),
    DONT_WORK_NOW("Продажи временно закрыты"),
    PRICE_MISMATCH("Некорректная цена"),
    OTHER("Другое (см. комментарий)")
}