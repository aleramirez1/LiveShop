# Debugging - Error 400 BAD REQUEST

## Posibles Causas

1. **Campos vacíos**: La app está enviando campos vacíos
2. **Formato incorrecto**: Los datos no están en el formato esperado
3. **Problema de serialización**: Kotlin no está serializando correctamente

## Cómo Verificar

### 1. Ver logs del API

Cuando intentes registrarte, el API imprimirá:
```
Datos recibidos: email=..., nombre=..., password=..., telefono=...
```

Esto te dirá exactamente qué está recibiendo el API.

### 2. Ver logs de la App

En Android Studio, abre `Logcat` y busca:
- `HttpLoggingInterceptor` - Verá el JSON que está enviando
- `RegisterViewModelImpl` - Verá los errores

### 3. Probar desde Python

```bash
python api/test_register.py
```

Si esto funciona, el problema está en la app.

## Soluciones

### Si el API recibe campos vacíos:
- Asegúrate de llenar TODOS los campos en la app
- Verifica que el teléfono solo contiene números

### Si el formato es incorrecto:
- Verifica que `RegisterRequest` en `LiveShopApi.kt` tiene los campos correctos
- Verifica que la serialización está configurada correctamente

### Si hay error de serialización:
- Asegúrate que `@Serializable` está en todas las data classes
- Verifica que los nombres de los campos coinciden exactamente

## Pasos para Resolver

1. Abre Android Studio
2. Ve a `View > Tool Windows > Logcat`
3. Intenta registrarte
4. Busca mensajes de error en Logcat
5. Comparte el error exacto

## Validación en la App

La app ahora valida:
- Email no vacío
- Nombre no vacío
- Teléfono no vacío
- Contraseña no vacío
- Las contraseñas coinciden

Si alguno de estos falla, el botón "Registrarse" estará deshabilitado.

---

**Última actualización**: Febrero 2026
