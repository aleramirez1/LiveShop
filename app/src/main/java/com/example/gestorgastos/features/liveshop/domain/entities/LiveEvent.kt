package com.example.liveshop.features.liveshop.domain.entities

sealed class LiveEvent {
    data class ProductUpdated(val product: Product) : LiveEvent()
    data class ProductDeleted(val productId: String) : LiveEvent()
    data class PriceChanged(val productId: String, val newPrice: Double) : LiveEvent()
    data class QuantityChanged(val productId: String, val newQuantity: Int) : LiveEvent()
    data class OrderCreated(val order: Order) : LiveEvent()
    data class ReservationCreated(val reservation: Reservation) : LiveEvent()
}
