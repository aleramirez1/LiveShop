# Marketplace con Pestañas

## 📱 Estructura

El Marketplace ahora tiene dos pestañas:

### 1. **Mis Productos**
- Muestra solo los productos que subiste
- Botones disponibles:
  - **Editar**: Para modificar el producto
  - **Eliminar**: Para eliminar el producto
- Contador: Muestra cuántos productos tienes

### 2. **Todos los Productos**
- Muestra todos los productos de otros vendedores
- Botón disponible:
  - **Comprar por SMS**: Para comprar el producto
- Contador: Muestra cuántos productos hay disponibles
- Los productos persisten aunque cierres sesión

## 🔄 Flujo

### Pestaña "Mis Productos"
1. Usuario ve sus productos
2. Puede hacer clic en una tarjeta para ver detalles
3. En la modal, ve botones "Editar" y "Eliminar"
4. Puede editar o eliminar el producto

### Pestaña "Todos los Productos"
1. Usuario ve productos de otros vendedores
2. Puede hacer clic en una tarjeta para ver detalles
3. En la modal, ve botón "Comprar por SMS"
4. Al presionar, se abre SMS y se crea una orden
5. El producto se actualiza en tiempo real

## 🎯 Características

- **Persistencia**: Los productos se guardan en el servidor
- **Tiempo Real**: Los cambios se reflejan automáticamente
- **Separación**: Tus productos vs productos de otros
- **Notificaciones**: Se muestra cuando se vende un producto
- **Acceso sin sesión**: Puedes ver y comprar productos sin estar logueado

## 📝 Implementación

**Archivo**: `MarketplaceScreenView.kt`

### Variables principales
```kotlin
val userId = viewModel.sessionManager.userId.value ?: 0
val myProducts = marketplaceState.products.filter { it.idVendedor == userId }
val allProducts = marketplaceState.products.filter { it.idVendedor != userId }
```

### Componentes
- `ProductCardItem`: Tarjeta para comprar (todos los productos)
- `MyProductCardItem`: Tarjeta para editar/eliminar (mis productos)
- `ProductDetailModal`: Modal con detalles y acciones

## 🚀 Próximos Pasos

1. Implementar funcionalidad de editar producto
2. Implementar funcionalidad de eliminar producto
3. Agregar confirmación antes de eliminar
4. Agregar historial de compras
