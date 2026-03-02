package com.example.liveshop_par.features.marketplace.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.data.network.LiveShopApi
import com.example.liveshop_par.data.network.PurchaseNotificationRequest
import com.example.liveshop_par.domain.model.Product
import com.example.liveshop_par.domain.usecase.GetAllProductsUseCase
import com.example.liveshop_par.domain.usecase.CreateProductUseCase
import com.example.liveshop_par.domain.usecase.GetAllProductByUserUseCase
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
    val notificationMessage: String? = null,
    val isSendingNotification: Boolean = false
)

// regla2: viewmodel inyectado con hilt, usa casos de uso de dominio
@HiltViewModel
class MarketplaceViewModelImpl @Inject constructor(
    val sessionManager: SessionManager,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getAllProductsByUserUseCase: GetAllProductByUserUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val api: LiveShopApi
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

    fun clearError() {
        _marketplaceState.value = _marketplaceState.value.copy(error = null)
    }

    fun resetSuccess() {
        _marketplaceState.value = _marketplaceState.value.copy(success = false)
    }

    fun sendPurchaseNotification(product: Product) {
        viewModelScope.launch {
            _marketplaceState.value = _marketplaceState.value.copy(isSendingNotification = true)
            try {
                val buyerId = sessionManager.userId.value ?: 0
                val buyerName = sessionManager.userName.value ?: ""
                val buyerNumber = sessionManager.userNumber.value ?: ""

                val request = PurchaseNotificationRequest(
                    productId = product.id,
                    vendorId = product.idVendedor,
                    buyerId = buyerId,
                    buyerName = buyerName,
                    buyerNumber = buyerNumber,
                    productName = product.nombre
                )

                val response = api.sendPurchaseNotification(request)
                
                if (response.isSuccessful) {
                    _marketplaceState.value = _marketplaceState.value.copy(
                        isSendingNotification = false,
                        notificationMessage = "Notificacion enviada al vendedor"
                    )
                } else {
                    _marketplaceState.value = _marketplaceState.value.copy(
                        isSendingNotification = false,
                        error = "Error al enviar notificacion"
                    )
                }
            } catch (e: Exception) {
                _marketplaceState.value = _marketplaceState.value.copy(
                    isSendingNotification = false,
                    error = e.message ?: "Error de conexion"
                )
            }
        }
    }

    fun clearNotification() {
        _marketplaceState.value = _marketplaceState.value.copy(notificationMessage = null)
    }
}
