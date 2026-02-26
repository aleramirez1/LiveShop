package com.example.liveshop.features.liveshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop.features.liveshop.domain.entities.LiveEvent
import com.example.liveshop.features.liveshop.domain.entities.Product
import com.example.liveshop.features.liveshop.domain.usecases.BuyProductUseCase
import com.example.liveshop.features.liveshop.domain.usecases.ConnectWebSocketUseCase
import com.example.liveshop.features.liveshop.domain.usecases.CreateProductUseCase
import com.example.liveshop.features.liveshop.domain.usecases.DeleteProductUseCase
import com.example.liveshop.features.liveshop.domain.usecases.DisconnectWebSocketUseCase
import com.example.liveshop.features.liveshop.domain.usecases.GetLiveEventsUseCase
import com.example.liveshop.features.liveshop.domain.usecases.GetProductsUseCase
import com.example.liveshop.features.liveshop.domain.usecases.UpdateProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveshopUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val selectedProduct: Product? = null
)

@HiltViewModel
class LiveshopViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getLiveEventsUseCase: GetLiveEventsUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val buyProductUseCase: BuyProductUseCase,
    private val connectWebSocketUseCase: ConnectWebSocketUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveshopUiState())
    val uiState: StateFlow<LiveshopUiState> = _uiState.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    private val _successEvents = MutableSharedFlow<String>()
    val successEvents = _successEvents.asSharedFlow()

    init {
        loadProducts()
        observeLiveEvents()
        connectWebSocket()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { products ->
                _uiState.value = _uiState.value.copy(products = products)
            }
        }
    }

    private fun observeLiveEvents() {
        viewModelScope.launch {
            getLiveEventsUseCase().collect { event ->
                when (event) {
                    is LiveEvent.ProductUpdated -> {
                        val updatedProducts = _uiState.value.products.map { product ->
                            if (product.id == event.product.id) event.product else product
                        }
                        _uiState.value = _uiState.value.copy(products = updatedProducts)
                    }

                    is LiveEvent.ProductDeleted -> {
                        val filteredProducts = _uiState.value.products.filter { it.id != event.productId }
                        _uiState.value = _uiState.value.copy(products = filteredProducts)
                    }

                    is LiveEvent.PriceChanged -> {
                        val updatedProducts = _uiState.value.products.map { product ->
                            if (product.id == event.productId) {
                                product.copy(price = event.newPrice)
                            } else product
                        }
                        _uiState.value = _uiState.value.copy(products = updatedProducts)
                    }

                    is LiveEvent.QuantityChanged -> {
                        val updatedProducts = _uiState.value.products.map { product ->
                            if (product.id == event.productId) {
                                product.copy(quantity = event.newQuantity)
                            } else product
                        }
                        _uiState.value = _uiState.value.copy(products = updatedProducts)
                    }

                    is LiveEvent.OrderCreated -> {
                        _successEvents.emit("Orden creada: ${event.order.id}")
                    }

                    is LiveEvent.ReservationCreated -> {
                        _successEvents.emit("Reserva creada: ${event.reservation.id}")
                    }
                }
            }
        }
    }

    fun selectProduct(product: Product) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = updateProductUseCase(product)
            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                _successEvents.emit("Producto actualizado")
            }.onFailure { error ->
                _errorEvents.emit("Error: ${error.message}")
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = deleteProductUseCase(productId)
            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                _successEvents.emit("Producto eliminado")
            }.onFailure { error ->
                _errorEvents.emit("Error: ${error.message}")
            }
        }
    }

    fun createProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = createProductUseCase(product)
            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                _successEvents.emit("Producto creado")
            }.onFailure { error ->
                _errorEvents.emit("Error: ${error.message}")
            }
        }
    }

    fun buyProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = buyProductUseCase(productId)
            _uiState.value = _uiState.value.copy(isLoading = false)

            result.onSuccess {
                _successEvents.emit("Compra realizada")
            }.onFailure { error ->
                _errorEvents.emit("Error: ${error.message}")
            }
        }
    }

    private fun connectWebSocket() {
        connectWebSocketUseCase()
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocketUseCase()
    }
}
