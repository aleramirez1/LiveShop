package com.example.liveshop_par.features.marketplace.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.liveshop_par.domain.model.Product
import com.example.liveshop_par.features.marketplace.presentation.viewmodels.MarketplaceViewModelImpl

@Composable
fun MarketplaceScreenView(
    viewModel: MarketplaceViewModelImpl,
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddProduct by remember { mutableStateOf(false) }
    val marketplaceState by viewModel.marketplaceState.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val context = LocalContext.current

    // Estado de las pestañas
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Explorar", "Mis Productos")

    // Cargar productos dependiendo de la pestaña seleccionada
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 0) {
            viewModel.loadAllProducts()
        } else {
            viewModel.loadMyProducts()
        }
    }

    // Recargar la lista si se crea un producto exitosamente
    LaunchedEffect(marketplaceState.success) {
        if (marketplaceState.success) {
            showAddProduct = false
            viewModel.resetSuccess()
            if (selectedTabIndex == 0) viewModel.loadAllProducts() else viewModel.loadMyProducts()
        }
    }

    if (showAddProduct) {
        AddProductModalView(
            onDismiss = { showAddProduct = false },
            onAddProduct = { nombre, precio, cantidad, descripcion, imagenUri ->
                showAddProduct = false
                viewModel.createProduct(nombre, precio, cantidad, descripcion, imagenUri, context)
            }
        )
    }

    if (selectedProduct != null) {
        ProductDetailModal(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null }
        )
    }

    Scaffold(
        floatingActionButton = {
            // El botón de agregar solo aparece en "Mis Productos"
            if (selectedTabIndex == 1) {
                FloatingActionButton(
                    onClick = { showAddProduct = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Cabecera
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Marketplace",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Button(onClick = onLogout) {
                    Text("Salir")
                }
            }

            // Barra de Pestañas
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar productos") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Filtro local basado en la búsqueda
                val filteredProducts = marketplaceState.products.filter {
                    it.nombre.contains(searchQuery, ignoreCase = true)
                }

                // Mostrar indicador de carga, estado vacío, o la cuadrícula
                if (marketplaceState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No hay resultados" else "No hay productos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (selectedTabIndex == 1 && searchQuery.isEmpty()) {
                                Text(
                                    text = "Presiona el + para agregar tu primer producto",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductCardItem(
                                product = product,
                                onClick = { selectedProduct = product }
                            )
                        }
                    }
                }
            }
        }
    }
}