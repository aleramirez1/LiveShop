# LiveShop - Android App

Aplicación móvil Android para LiveShop, una plataforma de compra y venta en tiempo real.

## Características

- **Clean Architecture**: Separación clara entre capas (Presentation, Domain, Data)
- **MVVM + Jetpack Compose**: UI moderna y reactiva
- **Hilt Dependency Injection**: Inyección de dependencias automática
- **Room Database**: Persistencia local de datos
- **Retrofit + OkHttp**: Comunicación con API REST
- **WebSocket**: Actualizaciones en tiempo real
- **Kotlin Flows**: Programación reactiva con StateFlow y SharedFlow
- **Actualización Optimista**: Cambios inmediatos en UI con rollback si falla

## Requisitos

- Android 8.0+ (API 26)
- Android Studio Jellyfish o superior
- JDK 17
- Gradle 8.7.0

## Instalación

1. Clona el repositorio:
```bash
git clone https://github.com/aleramirez1/LiveShop_front.git
cd LiveShop_front
```

2. Abre el proyecto en Android Studio

3. Espera a que termine la sincronización de Gradle

4. Ejecuta la app:
   - Click en "Run" (Shift + F10)
   - Selecciona emulador o dispositivo

## Configuración

### Servidores Requeridos

Antes de ejecutar la app, asegúrate que estos servidores estén corriendo:

1. **API Python** (http://10.0.2.2:8000/):
   - Repositorio: LiveShop_backend
   - Comando: `python run.py`

2. **WebSocket Node.js** (ws://10.0.2.2:8000/):
   - Repositorio: LiveShop_websocket
   - Comando: `npm start`

3. **MySQL Database**:
   - Ver schema en repositorio backend

## Estructura del Proyecto

```
app/src/main/java/com/example/gestorgastos/
├── core/
│   ├── di/              # Módulos de inyección de dependencias
│   ├── navigation/      # Configuración de navegación
│   ├── network/         # Configuración de API
│   └── ui/              # Tema y estilos
├── features/
│   ├── auth/            # Autenticación
│   │   ├── data/        # Repositorios y data sources
│   │   ├── domain/      # Use cases y interfaces
│   │   └── presentation/# Screens y ViewModels
│   ├── register/        # Registro de usuarios
│   └── liveshop/        # Funcionalidad principal
│       ├── data/        # Room, Retrofit, WebSocket
│       ├── domain/      # Entidades y use cases
│       └── presentation/# Screens y ViewModels
└── MainActivity.kt      # Punto de entrada
```

## Flujo de la App

1. **Login Screen**: Ingresa credenciales
2. **Register Screen**: Crea nueva cuenta
3. **Liveshop Screen**: 
   - Ver productos en tiempo real
   - Comprar productos
   - Editar/Eliminar productos
   - Reservar productos
   - Ver órdenes

## Tecnologías

- **Kotlin 2.0.10**: Lenguaje de programación
- **Jetpack Compose**: Framework UI
- **Hilt 2.51.1**: Inyección de dependencias
- **Room 2.6.1**: Base de datos local
- **Retrofit 2.9.0**: Cliente HTTP
- **OkHttp 4.12.0**: Interceptor HTTP
- **Coroutines 1.8.1**: Programación asíncrona
- **Coil 2.6.0**: Carga de imágenes

## Compilación

### Debug
```bash
./gradlew build
./gradlew installDebug
```

### Release
```bash
./gradlew build -Pbuild_type=release
```

## Troubleshooting

### Error de Gradle
```bash
./gradlew clean
./gradlew build
```

### Invalidar caches en Android Studio
- File → Invalidate Caches → Invalidate and Restart

### Verificar emulador
- Abre Android Virtual Device Manager
- Crea o inicia un emulador

## Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la licencia MIT.

## Autor

Alejandro Ramírez

## Contacto

Para preguntas o sugerencias, contacta a través de GitHub Issues.
