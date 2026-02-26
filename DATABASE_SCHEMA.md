# Esquema de Base de Datos - LiveShop

## BD Remota (liveshop.myddns.me)

### Tabla: usuarios
```sql
CREATE TABLE usuarios (
    iduser INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    number VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tabla: products
```sql
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    seller_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    img_url VARCHAR(500),
    description VARCHAR(500),
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES usuarios(iduser)
);
```

### Tabla: orders
```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    buyer_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    is_delivered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES usuarios(iduser),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

## BD Local (Android - Room)

### Tabla: users
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val email: String,
    val name: String,
    val password: String,
    val phone: String,
    val createdAt: Long
)
```

### Tabla: products
```kotlin
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUri: String,
    val availableUnits: Int,
    val sellerId: Int,
    val sellerName: String,
    val sellerPhone: String,
    val createdAt: Long
)
```

### Tabla: purchases
```kotlin
@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val buyerId: Int,
    val productId: Int,
    val quantity: Int,
    val totalPrice: Double,
    val purchaseDate: String
)
```

## Flujo de Datos

1. **Registro/Login**: App → API Go → BD Remota
2. **Obtener Productos**: App → API Go → BD Remota → App (cachea en BD Local)
3. **Crear Producto**: App → API Go → BD Remota → App (cachea en BD Local)
4. **Comprar Producto**: App → API Go → BD Remota (crea orden)

## Datos de Prueba

Ejecutar en BD remota:
```bash
mysql -h liveshop.myddns.me -u liveshop -p liveshop < api/seed_data.sql
```

Usuarios de prueba:
- juan@example.com / password123
- maria@example.com / password123
- carlos@example.com / password123
- ana@example.com / password123

Productos de prueba: 8 productos en diferentes categorías
