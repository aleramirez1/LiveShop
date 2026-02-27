# LiveShop - Guía de Configuración y Ejecución

## 📋 Requisitos Previos

- Android Studio instalado
- MySQL Server corriendo en `localhost:3306`
- Python 3.8+ instalado
- Node.js instalado (para WebSocket)

## 🚀 Pasos para Ejecutar

### 1. Inicializar Base de Datos

```bash
cd api
python reset_db.py
```

Esto creará la base de datos `liveshop` con las tablas `usuarios` y `productos`.

### 2. Ejecutar API Flask

```bash
cd api
python run.py
```

La API estará disponible en:
- Desde tu PC: `http://localhost:5000`
- Desde emulador Android: `http://192.168.100.23:5000`

### 3. Ejecutar WebSocket Server (Opcional)

```bash
cd websocket
npm install
node main.js
```

El WebSocket estará disponible en:
- Desde tu PC: `ws://localhost:8080`
- Desde emulador Android: `ws://192.168.100.23:8080`

### 4. Ejecutar App Android

1. Abre Android Studio
2. Abre el proyecto `app/`
3. Selecciona un emulador o dispositivo
4. Presiona `Run` o `Shift + F10`

## 🔌 Configuración de Puertos

| Servicio | Puerto | URL desde Emulador |
|----------|--------|-------------------|
| API Flask | 5000 | http://192.168.100.23:5000 |
| WebSocket | 8080 | ws://192.168.100.23:8080 |
| MySQL | 3306 | localhost:3306 |

**Nota**: Reemplaza `192.168.100.23` con tu IP local si es diferente.

## 📱 Flujo de Uso

### Registro
1. Abre la app
2. Ve a "Registrarse"
3. Completa los campos:
   - Nombre
   - Email
   - Teléfono
   - Contraseña
   - Confirmar Contraseña
4. Presiona "Registrarse"

### Login
1. Ingresa tu email y contraseña
2. Presiona "Iniciar Sesión"

### Comprar Producto
1. Ve a "LiveShop"
2. Busca o filtra productos
3. Presiona en un producto
4. Presiona "Llamar" para contactar al vendedor

## 🧪 Pruebas de API

### Registrar Usuario

```bash
python api/test_register.py
```

### Verificar Salud de API

```bash
curl http://localhost:5000/health
```

## 🐛 Solución de Problemas

### Error: "CLEARTEXT communication not permitted"
- Asegúrate que `network_security_config.xml` está en `app/src/main/res/xml/`
- Verifica que `AndroidManifest.xml` referencia `android:networkSecurityConfig="@xml/network_security_config"`

### Error: "Connection refused"
- Verifica que el API está corriendo: `python api/run.py`
- Verifica que MySQL está corriendo
- Verifica que usas `10.0.2.2` desde el emulador (no `localhost`)

### Error: "Email ya registrado"
- Usa un email diferente para cada registro
- O ejecuta `python api/reset_db.py` para limpiar la BD

## 📝 Notas

- La app usa Hilt para inyección de dependencias
- La arquitectura sigue Clean Architecture (Presentation, Domain, Data)
- Los datos se sincronizan entre API y BD local
- El WebSocket proporciona actualizaciones en tiempo real

---

**Última actualización**: Febrero 2026
