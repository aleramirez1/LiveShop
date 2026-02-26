package com.example.liveshop_par.features.liveshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.features.liveshop.domain.entities.Product
import com.example.liveshop_par.features.liveshop.domain.usecases.AddProductUseCase
import com.example.liveshop_par.features.liveshop.domain.usecases.GetAllProductsUseCase
import com.example.liveshop_par.features.liveshop.domain.usecases.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveShopUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedProduct: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class LiveShopViewModelImpl @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveShopUiState())
    val uiState: StateFlow<LiveShopUiState> = _uiState.asStateFlow()

    init {
        loadAllProducts()
    }

    private fun loadAllProducts() {
        viewModelScope.launch {
            getAllProductsUseCase().collect { products ->
                val categories = products.map { it.category }.distinct().sorted()
                _uiState.value = _uiState.value.copy(
                    products = products,
                    categories = categories
                )
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = addProductUseCase(product)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                loadAllProducts()
            }
            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Error al agregar producto"
                )
            }
        }
    }

    fun selectProduct(product: Product) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun clearSelectedProduct() {
        _uiState.value = _uiState.value.copy(selectedProduct = null)
    }

    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadAllProducts()
            } else {
                searchProductsUseCase(query).collect { products ->
                    _uiState.value = _uiState.value.copy(products = products)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
