package com.example.liveshop_par.features.marketplace.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.data.network.WebSocketManager
import com.example.liveshop_par.domain.model.Product
import com.example.liveshop_par.domain.usecase.GetAllProductsUseCase
import com.example.liveshop_par.domain.usecase.CreateProductUseCase
import com.example.liveshop_par.domain.usecase.CreateOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketplaceState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val products: List<Product> = emptyList(),
    val notification: String? = null
)

// regla2: viewmodel inyectado con hilt, usa casos de uso de dominio
@HiltViewModel
class MarketplaceViewModelImpl @Inject constructor(
    val sessionManager: SessionManager,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _marketplaceState = MutableStateFlow(MarketplaceState())
    val marketplaceState: StateFlow<MarketplaceState> = _marketplaceState

    init {
        webSocketManager.connect()
        listenToWebSocketEvents()
    }

    private fun listenToWebSocketEvents() {
        viewModelScope.launch {
            webSocketManager.events.collect { event ->
                when (event.type) {
                    "product_created" -> {
                        event.product?.let { productData ->
                            val newProduct = Product(
                                id = productData.id ?: 0,
                                nombre = productData.nombre ?: "",
                                precio = productData.precio ?: 0.0,
                                stock = productData.stock ?: 0,
                                imagen = productData.imagen?.let { Uri.parse(it) },
                                nombreVendedor = productData.nombre_vendedor ?: "",
                                numeroVendedor = productData.numero_vendedor ?: "",
                                idVendedor = productData.id_vendedor ?: 0,
                                descripcion = productData.descripcion ?: "",
                                categoria = productData.categoria ?: ""
                            )
                            val updatedProducts = _marketplaceState.value.products + newProduct
                            _marketplaceState.value = _marketplaceState.value.copy(
                                products = updatedProducts,
                                notification = "Nuevo producto: ${productData.nombre}"
                            )
                            kotlinx.coroutines.delay(3000)
                            _marketplaceState.value = _marketplaceState.value.copy(notification = null)
                        }
                    }
                    "product_sold" -> {
                        event.productId?.let { productId ->
                            val updatedProducts = _marketplaceState.value.products.map { product ->
                                if (product.id == productId) {
                                    val newStock = (event.stock ?: 0)
                                    if (newStock <= 0) {
                                        product.copy(stock = 0)
                                    } else {
                                        product.copy(stock = newStock)
                                    }
                                } else {
                                    product
                                }
                            }
                            _marketplaceState.value = _marketplaceState.value.copy(
                                products = updatedProducts,
                                notification = "Producto vendido: ${updatedProducts.find { it.id == productId }?.nombre}"
                            )
                            kotlinx.coroutines.delay(3000)
                            _marketplaceState.value = _marketplaceState.value.copy(notification = null)
                        }
                    }
                }
            }
        }
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            _marketplaceState.value = _marketplaceState.value.copy(isLoading = true, error = null)
            getAllProductsUseCase().collect { result ->
                result.onSuccess { products ->
                    _marketplaceState.value = MarketplaceState(
                        isLoading = false,
                        success = true,
                        products = products
                    )
                }
                result.onFailure { exception ->
                    _marketplaceState.value = _marketplaceState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar productos"
                    )
                }
            }
        }
    }

    fun createProduct(
        nombre: String,
        precio: Double,
        cantidad: Int,
        descripcion: String,
        categoria: String,
        imagenUri: Uri?,
        context: Context? = null
    ) {
        viewModelScope.launch {
            _marketplaceState.value = _marketplaceState.value.copy(isLoading = true, error = null)
            try {
                val nombreVendedor = sessionManager.userName.value ?: ""
                val numeroVendedor = sessionManager.userNumber.value ?: ""
                val userId = sessionManager.userId.value ?: 0
                
                val newProduct = Product(
                    id = System.currentTimeMillis().toInt(),
                    nombre = nombre,
                    precio = precio,
                    stock = cantidad,
                    imagen = imagenUri,
                    nombreVendedor = nombreVendedor,
                    numeroVendedor = numeroVendedor,
                    idVendedor = userId,
                    descripcion = descripcion,
                    categoria = categoria
                )
                
                val updatedProducts = _marketplaceState.value.products + newProduct
                _marketplaceState.value = _marketplaceState.value.copy(
                    isLoading = false,
                    success = true,
                    products = updatedProducts
                )
                
                val product = Product(
                    id = 0,
                    nombre = nombre,
                    precio = precio,
                    stock = cantidad,
                    imagen = imagenUri,
                    nombreVendedor = nombreVendedor,
                    numeroVendedor = numeroVendedor,
                    idVendedor = userId,
                    descripcion = descripcion,
                    categoria = categoria
                )
                
                createProductUseCase(product).collect { result ->
                    result.onSuccess { createdProduct ->
                    }
                    result.onFailure { exception ->
                        _marketplaceState.value = _marketplaceState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al crear producto"
                        )
                    }
                }
            } catch (e: Exception) {
                _marketplaceState.value = _marketplaceState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error de conexion"
                )
            }
        }
    }

    fun createOrder(productId: Int, vendorId: Int, quantity: Int, buyerNumber: String) {
        viewModelScope.launch {
            val buyerId = sessionManager.userId.value ?: 0
            createOrderUseCase(productId, vendorId, buyerId, quantity, buyerNumber).collect { result ->
                result.onSuccess {
                }
                result.onFailure { exception ->
                    _marketplaceState.value = _marketplaceState.value.copy(
                        error = exception.message ?: "Error al crear orden"
                    )
                }
            }
        }
    }

    fun clearError() {
        _marketplaceState.value = _marketplaceState.value.copy(error = null)
    }

    fun resetSuccess() {
        _marketplaceState.value = _marketplaceState.value.copy(success = false)
    }

    fun clearNotification() {
        _marketplaceState.value = _marketplaceState.value.copy(notification = null)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}
