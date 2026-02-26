package com.example.liveshop_par.features.liveshop.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.liveshop_par.features.liveshop.domain.entities.Product
import com.example.liveshop_par.features.liveshop.presentation.viewmodels.LiveShopViewModelImpl

@Composable
fun AddProductDialogView(
    userId: Int,
    onDismiss: () -> Unit,
    onProductAdded: () -> Unit,
    viewModel: LiveShopViewModelImpl
) {
    var step by remember { mutableStateOf(1) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var units by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (step == 1) "Agregar Producto - Paso 1" else "Agregar Producto - Paso 2",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (step == 1) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Imagen del producto",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(if (imageUri != null) "Cambiar Imagen" else "Subir Imagen")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Nombre del Producto *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Precio *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = units,
                        onValueChange = { units = it.filter { c -> c.isDigit() } },
                        label = { Text("Unidades Disponibles *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { 
                            if (it.length <= 100) {
                                description = it
                            }
                        },
                        label = { Text("Descripción * (${description.length}/100)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        minLines = 3,
                        maxLines = 4
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    Text(
                        text = "* Campos obligatorios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            if (step == 1) {
                Button(
                    onClick = { step = 2 },
                    enabled = imageUri != null && productName.isNotEmpty()
                ) {
                    Text("Siguiente")
                }
            } else {
                Button(
                    onClick = {
                        val product = Product(
                            name = productName,
                            price = price.toDoubleOrNull() ?: 0.0,
                            description = description,
                            category = category,
                            imageUri = imageUri.toString(),
                            availableUnits = units.toIntOrNull() ?: 0,
                            sellerId = userId
                        )
                        viewModel.addProduct(product)
                        onProductAdded()
                    },
                    enabled = price.isNotEmpty() && units.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty()
                ) {
                    Text("Subir")
                }
            }
        },
        dismissButton = {
            Button(onClick = {
                if (step == 2) {
                    step = 1
                } else {
                    onDismiss()
                }
            }) {
                Text(if (step == 1) "Cancelar" else "Atrás")
            }
        }
    )
}
