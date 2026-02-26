# Conexión API - Emulador Android

## 🔧 Configuración Actual

**IP del Host**: `192.168.100.23`
**Puerto API**: `5000`
**Puerto WebSocket**: `8080`

## ✅ Lo que está configurado

### En la App Android:
- `ApiClient.kt` usa: `http://192.168.100.23:5000/`
- `WebSocketManager.kt` usa: `ws://192.168.100.23:8080`
- `AndroidManifest.xml` tiene permisos de INTERNET
- `network_security_config.xml` permite cleartext

### En el API:
- Flask escucha en `0.0.0.0:5000` (accesible desde cualquier IP)
- Base de datos MySQL en `localhost:3306`
- Todos los endpoints funcionan correctamente

## 🚀 Pasos para Ejecutar

### 1. Asegúrate que el API está corriendo
```bash
cd api
python run.py
```

Deberías ver:
```
✓ Servidor escuchando en puerto 5000
✓ API: http://localhost:5000
✓ API (desde emulador): http://192.168.100.23:5000
```

### 2. Abre el emulador Android
- Asegúrate que está en la misma red que tu PC
- O usa un dispositivo físico conectado a la misma red

### 3. Abre la app y prueba el registro
- Ve a "Registrarse"
- Completa los campos
- Presiona "Registrarse"

## 🔍 Verificación

### Probar API desde tu PC:
```bash
python api/test_register.py
```

### Probar conexión desde emulador:
En Android Studio, abre la consola y ejecuta:
```bash
adb shell ping 192.168.100.23
```

## ⚠️ Si no funciona

### Error: "failed to connect to /192.168.100.23"
1. Verifica que el API está corriendo: `python api/run.py`
2. Verifica que usas la IP correcta (no `10.0.2.2`)
3. Verifica que el firewall no bloquea el puerto 5000
4. Verifica que el emulador está en la misma red

### Para permitir puerto en Windows Firewall:
```powershell
netsh advfirewall firewall add rule name="Flask API" dir=in action=allow protocol=tcp localport=5000
```

### Para verificar que el API está escuchando:
```bash
netstat -an | findstr 5000
```

## 📝 Notas

- La IP `192.168.100.23` es específica de tu red
- Si cambias de red, necesitarás actualizar la IP en:
  - `app/src/main/java/com/example/liveshop_par/data/network/ApiClient.kt`
  - `app/src/main/java/com/example/liveshop_par/data/network/WebSocketManager.kt`
- El alias `10.0.2.2` solo funciona en emuladores de Android Studio en ciertos casos

---

**Última actualización**: Febrero 2026
