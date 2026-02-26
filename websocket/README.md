# 🔌 WebSocket Server - LiveShop

Servidor WebSocket en tiempo real para actualizaciones de productos en LiveShop.

## 📋 Descripción

El servidor WebSocket permite:
- ✅ Conexión en tiempo real
- ✅ Actualizaciones de productos en vivo
- ✅ Notificaciones de cambios
- ✅ Sincronización entre clientes
- ✅ Reconexión automática

## 🚀 Instalación

### 1. Instalar dependencias
```bash
cd websocket
npm install
```

### 2. Configurar variables de entorno
```bash
cp .env.example .env
```

Editar `.env`:
```
PORT=8080
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=tu_contraseña
DB_NAME=liveshop
DB_PORT=3306
```

### 3. Ejecutar servidor
```bash
npm start
```

O en modo desarrollo con nodemon:
```bash
npm run dev
```

## 📡 Eventos WebSocket

### Cliente → Servidor

#### Suscribirse a productos
```javascript
{
  type: 'subscribe_products'
}
```

#### Suscribirse a usuario
```javascript
{
  type: 'subscribe_user',
  userId: 1
}
```

#### Notificar producto creado
```javascript
{
  type: 'product_created',
  product: {
    id: 1,
    nombre: 'Laptop',
    precio: 999.99,
    ...
  }
}
```

#### Notificar producto actualizado
```javascript
{
  type: 'product_updated',
  product: { ... }
}
```

#### Notificar producto eliminado
```javascript
{
  type: 'product_deleted',
  product: { ... }
}
```

#### Ping (verificar conexión)
```javascript
{
  type: 'ping'
}
```

### Servidor → Cliente

#### Conexión establecida
```javascript
{
  type: 'connection',
  message: 'Conectado al servidor WebSocket de LiveShop',
  timestamp: '2026-02-26T10:00:00.000Z'
}
```

#### Lista de productos
```javascript
{
  type: 'products_list',
  products: [ ... ],
  timestamp: '2026-02-26T10:00:00.000Z'
}
```

#### Datos de usuario
```javascript
{
  type: 'user_data',
  user: {
    id: 1,
    nombre: 'Juan',
    email: 'juan@example.com',
    telefono: '1234567890'
  },
  timestamp: '2026-02-26T10:00:00.000Z'
}
```

#### Producto creado (broadcast)
```javascript
{
  type: 'product_created',
  product: { ... },
  timestamp: '2026-02-26T10:00:00.000Z'
}
```

#### Producto actualizado (broadcast)
```javascript
{
  type: 'product_updated',
  product: { ... },
  timestamp: '2026-02-26T10:00:00.000Z'
}
```

#### Producto eliminado (broadcast)
```javascript
{
  type: 'product_deleted',
  product: { ... },
  timestamp: '2026-02-26T10:00:00.000Z'
}
```

#### Pong (respuesta a ping)
```javascript
{
  type: 'pong',
  timestamp: '2026-02-26T10:00:00.000Z'
}
```

#### Error
```javascript
{
  type: 'error',
  message: 'Descripción del error'
}
```

## 💻 Uso del Cliente

### Conectar
```javascript
const client = new LiveShopWebSocketClient('ws://localhost:8080');
await client.connect();
```

### Suscribirse a productos
```javascript
client.subscribeToProducts();

client.on('products_list', (data) => {
  console.log('Productos:', data.products);
});
```

### Suscribirse a usuario
```javascript
client.subscribeToUser(1);

client.on('user_data', (data) => {
  console.log('Usuario:', data.user);
});
```

### Escuchar actualizaciones
```javascript
client.on('product_created', (data) => {
  console.log('Nuevo producto:', data.product);
});

client.on('product_updated', (data) => {
  console.log('Producto actualizado:', data.product);
});

client.on('product_deleted', (data) => {
  console.log('Producto eliminado:', data.product);
});
```

### Notificar cambios
```javascript
// Cuando se crea un producto
client.notifyProductCreated({
  id: 1,
  nombre: 'Laptop',
  precio: 999.99,
  ...
});

// Cuando se actualiza
client.notifyProductUpdated({...});

// Cuando se elimina
client.notifyProductDeleted({...});
```

### Desconectar
```javascript
client.disconnect();
```

### Verificar estado
```javascript
const status = client.getStatus();
console.log(status);
// { isConnected: true, url: 'ws://localhost:8080', readyState: 1 }
```

## 🔄 Flujo de Datos

```
┌─────────────────────────────────────────┐
│  Cliente Android / Web                  │
│  (LiveShop App)                         │
└────────────┬────────────────────────────┘
             │
             │ WebSocket
             │ (ws://localhost:8080)
             ▼
┌─────────────────────────────────────────┐
│  WebSocket Server (Node.js)             │
│  • Maneja conexiones                    │
│  • Broadcast de eventos                 │
│  • Sincronización en tiempo real        │
└────────────┬────────────────────────────┘
             │
             │ MySQL Query
             │
             ▼
┌─────────────────────────────────────────┐
│  Base de Datos MySQL                    │
│  • usuarios                             │
│  • productos                            │
│  • eventos_en_vivo                      │
└─────────────────────────────────────────┘
```

## 🧪 Testing

### Con curl (verificar servidor HTTP)
```bash
curl http://localhost:8080/health
```

Respuesta:
```json
{
  "status": "ok",
  "message": "WebSocket server funcionando",
  "connectedClients": 0
}
```

### Con wscat (cliente WebSocket CLI)
```bash
npm install -g wscat
wscat -c ws://localhost:8080
```

Luego enviar:
```
{"type": "subscribe_products"}
```

### Con cliente JavaScript
```javascript
const client = new LiveShopWebSocketClient('ws://localhost:8080');
await client.connect();
client.subscribeToProducts();
```

## 📊 Estadísticas

Ver estadísticas de conexiones:
```bash
curl http://localhost:8080/stats
```

Respuesta:
```json
{
  "connectedClients": 5,
  "timestamp": "2026-02-26T10:00:00.000Z"
}
```

## 🔒 Características de Seguridad

- ✅ Validación de mensajes JSON
- ✅ Manejo de errores robusto
- ✅ Desconexión automática en errores
- ✅ Reconexión automática del cliente
- ✅ Logging de eventos

## 🚀 Próximas Mejoras

- [ ] Autenticación JWT
- [ ] Salas (rooms) por categoría
- [ ] Compresión de mensajes
- [ ] Rate limiting
- [ ] Persistencia de eventos
- [ ] Historial de cambios

## 📝 Notas

1. **Reconexión automática**: El cliente intenta reconectar hasta 5 veces
2. **Broadcast**: Los eventos se envían a todos los clientes conectados
3. **Sincronización**: Los datos se obtienen de la BD en tiempo real
4. **Escalabilidad**: Usar Redis para múltiples instancias del servidor

## 🆘 Troubleshooting

| Problema | Solución |
|----------|----------|
| `ECONNREFUSED` | Verificar que MySQL está corriendo |
| `Port already in use` | Cambiar PORT en .env |
| `Cannot find module` | Ejecutar `npm install` |
| `Connection refused` | Verificar URL del servidor |

---

**Versión**: 1.0  
**Última actualización**: Febrero 2026  
**Estado**: Listo para usar
