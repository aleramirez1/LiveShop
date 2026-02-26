package com.example.liveshop.core.network

import com.example.liveshop.features.liveshop.data.datasources.remote.model.OrderDto
import com.example.liveshop.features.liveshop.data.datasources.remote.model.ProductDto
import com.example.liveshop.features.liveshop.data.datasources.remote.model.ReservationDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LiveshopApi {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): ProductDto

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<ProductDto>

    @POST("products")
    suspend fun createProduct(@Body product: ProductDto): ProductDto

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: ProductDto): ProductDto

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String)

    @POST("products/{id}/buy")
    suspend fun buyProduct(@Path("id") id: String): ProductDto

    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto

    @POST("orders")
    suspend fun createOrder(@Body order: OrderDto): OrderDto

    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") id: String, @Body order: OrderDto): OrderDto

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: String)

    @GET("reservations")
    suspend fun getReservations(): List<ReservationDto>

    @GET("reservations/{id}")
    suspend fun getReservation(@Path("id") id: String): ReservationDto

    @POST("reservations")
    suspend fun createReservation(@Body reservation: ReservationDto): ReservationDto

    @DELETE("reservations/{id}")
    suspend fun deleteReservation(@Path("id") id: String)
}
