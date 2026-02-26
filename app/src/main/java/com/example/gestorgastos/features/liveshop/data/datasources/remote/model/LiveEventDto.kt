package com.example.liveshop.features.liveshop.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

sealed class LiveEventDto {
    data class ProductUpdatedDto(
        @SerializedName("type")
        val type: String = "PRODUCT_UPDATED",
        @SerializedName("product")
        val product: ProductDto
    ) : LiveEventDto()

    data class ProductDeletedDto(
        @SerializedName("type")
        val type: String = "PRODUCT_DELETED",
        @SerializedName("productId")
        val productId: String
    ) : LiveEventDto()

    data class PriceChangedDto(
        @SerializedName("type")
        val type: String = "PRICE_CHANGED",
        @SerializedName("productId")
        val productId: String,
        @SerializedName("newPrice")
        val newPrice: Double
    ) : LiveEventDto()

    data class QuantityChangedDto(
        @SerializedName("type")
        val type: String = "QUANTITY_CHANGED",
        @SerializedName("productId")
        val productId: String,
        @SerializedName("newQuantity")
        val newQuantity: Int
    ) : LiveEventDto()

    data class OrderCreatedDto(
        @SerializedName("type")
        val type: String = "ORDER_CREATED",
        @SerializedName("order")
        val order: OrderDto
    ) : LiveEventDto()

    data class ReservationCreatedDto(
        @SerializedName("type")
        val type: String = "RESERVATION_CREATED",
        @SerializedName("reservation")
        val reservation: ReservationDto
    ) : LiveEventDto()
}
