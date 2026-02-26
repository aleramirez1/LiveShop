# Seguridad - LiveShop

## Encriptación de Contraseñas

### Algoritmo: Bcrypt
- **Rounds**: 14 (nivel de seguridad alto)
- **Formato**: `$2a$14$...` (60 caracteres)
- **Irreversible**: No se pueden recuperar contraseñas originales

### Hash de Prueba
```
Contraseña original: password123
Hash bcrypt: $2a$14$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm
```

## Flujo de Autenticación

1. **Registro**:
   - Usuario envía: `{name, number, password}`
   - API Go encripta con bcrypt
   - BD almacena: hash bcrypt (nunca la contraseña original)

2. **Login**:
   - Usuario envía: `{number, password}`
   - API Go compara: `bcrypt.Compare(password_ingresado, hash_bd)`
   - Si coincide: genera JWT token
   - Si no coincide: rechaza login

3. **Operaciones Posteriores**:
   - App envía: JWT token en header `Authorization: Bearer <token>`
   - API Go valida token
   - Si válido: permite operación

## Datos Sensibles

### Protegidos ✅
- Contraseñas: Encriptadas con bcrypt
- Tokens JWT: Firmados con secret key
- Conexión API: HTTP (en desarrollo), HTTPS (en producción)

### No Encriptados (Necesarios)
- Números de teléfono: Necesarios para contacto directo
- Nombres: Necesarios para identificación
- Descripciones de productos: Información pública

## Recomendaciones de Producción

1. **HTTPS obligatorio**: Todas las conexiones deben ser HTTPS
2. **JWT Secret**: Cambiar `JWT_SECRET` en `.env` a un valor único y fuerte
3. **CORS**: Configurar CORS solo para dominios autorizados
4. **Rate Limiting**: Implementar límite de intentos de login
5. **Logs**: Registrar intentos fallidos de autenticación
6. **Backup**: Hacer backup regular de la BD

## Verificación de Seguridad

Para verificar que las contraseñas están encriptadas:

```sql
SELECT nombre, number, password FROM usuarios;
```

Deberías ver contraseñas como:
```
$2a$14$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm
```

NO deberías ver:
```
password123
```

## Generador de Hashes Bcrypt

Si necesitas generar nuevos hashes:

```bash
python api/generate_password_hashes.py
```

O usar online: https://bcrypt-generator.com/
