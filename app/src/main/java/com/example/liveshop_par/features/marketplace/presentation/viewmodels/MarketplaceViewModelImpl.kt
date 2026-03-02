package com.example.liveshop_par.features.marketplace.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.data.network.WsNotification
import com.example.liveshop_par.domain.model.Order
import com.example.liveshop_par.domain.model.Product
import com.example.liveshop_par.domain.usecase.CreateOrderUseCase
import com.example.liveshop_par.domain.usecase.GetAllProductsUseCase
import com.example.liveshop_par.domain.usecase.CreateProductUseCase
import com.example.liveshop_par.domain.usecase.GetAllProductByUserUseCase
import com.example.liveshop_par.domain.usecase.ObserveNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject


data class MarketplaceState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val products: List<Product> = emptyList()
)

// regla2: viewmodel inyectado con hilt, usa casos de uso de dominio
@HiltViewModel
class MarketplaceViewModelImpl @Inject constructor(
    val sessionManager: SessionManager,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getAllProductsByUserUseCase: GetAllProductByUserUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val observeNotificationsUseCase: ObserveNotificationsUseCase
) : ViewModel() {

    private val _marketplaceState = MutableStateFlow(MarketplaceState())
    val marketplaceState: StateFlow<MarketplaceState> = _marketplaceState

    fun loadAllProducts() {
        viewModelScope.launch {
            _marketplaceState.value = _marketplaceState.value.copy(isLoading = true, error = null)
            getAllProductsUseCase().collect { result ->
                result.onSuccess { products ->
                    _marketplaceState.value = _marketplaceState.value.copy(
                        isLoading = false,
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

    fun loadMyProducts() {
        viewModelScope.launch {
            _marketplaceState.value = _marketplaceState.value.copy(isLoading = true, error = null)
            getAllProductsByUserUseCase().collect { result ->
                result.onSuccess { products ->
                    _marketplaceState.value = _marketplaceState.value.copy(
                        isLoading = false,
                        products = products
                    )
                }
                result.onFailure { exception ->
                    _marketplaceState.value = _marketplaceState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar tus productos"
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
        imagenUri: Uri?,
        context: Context? = null
    ) {
        viewModelScope.launch {
            _marketplaceState.value = _marketplaceState.value.copy(isLoading = true, error = null)
            try {
                val nombreVendedor = sessionManager.userName.value ?: ""
                val numeroVendedor = sessionManager.userNumber.value ?: ""
                val userId = sessionManager.userId.value ?: 0
                
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
                )
                
                createProductUseCase(product).collect { result ->
                    result.onSuccess { createdProduct ->
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
                        )
                        
                        val updatedProducts = _marketplaceState.value.products + newProduct
                        _marketplaceState.value = _marketplaceState.value.copy(
                            isLoading = false,
                            success = true,
                            products = updatedProducts
                        )
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

    fun createOrder(productId: Int, cantidad: Int = 1) {
        viewModelScope.launch {
            _marketplaceState.value = _marketplaceState.value.copy(isLoading = true, error = null)

            try {
                val order = Order(
                    idorder = 0,
                    productoid = productId,
                    cantidad = cantidad,
                    vendedorid = 0,
                    vendedornombre = "",
                    vendedornumero = "",
                    entregado = false ,
                    creacion = ""
                )

                createOrderUseCase(order).collect { result ->
                    result.onSuccess {
                        _marketplaceState.value = _marketplaceState.value.copy(
                            isLoading = false,
                            success = true
                        )
                    }
                    result.onFailure { exception ->
                        _marketplaceState.value = _marketplaceState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al procesar la compra"
                        )
                    }
                }
            } catch (e: Exception) {
                _marketplaceState.value = _marketplaceState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error de conexión"
                )
            }
        }
    }

    fun clearError() {
        _marketplaceState.value = _marketplaceState.value.copy(error = null)
    }

    fun resetSuccess() {
        _marketplaceState.value = _marketplaceState.value.copy(success = false)
    }

    private val _notificationEvent = MutableSharedFlow<WsNotification>()
    val notificationEvent = _notificationEvent.asSharedFlow()

    fun connectToWebSocket() {
        val token = sessionManager.token.value ?: return

        viewModelScope.launch {
            observeNotificationsUseCase(token).collect { jsonString ->
                try {

                    val jsonParser = Json { ignoreUnknownKeys = true }
                    val notification = jsonParser.decodeFromString<WsNotification>(jsonString)

                    // 3. Emitimos el evento hacia la pantalla
                    _notificationEvent.emit(notification)
                } catch (e: Exception) {
                    println("Error leyendo WS: ${e.message}")
                }
            }
        }
    }
}
