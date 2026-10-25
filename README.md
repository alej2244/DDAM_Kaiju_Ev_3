# Kaiju Inventario

Aplicación Android para controlar el inventario de Kaiju. Permite registrar productos, llevar el stock al día con entradas y salidas, y avisa cuando un producto queda con pocas unidades para reponerlo a tiempo.

Integrante: Alejandro Naranjo · Sección DSY1105 · Grupo 13

## Funcionalidades

- Lista de productos con resumen del total y de los productos con stock bajo.
- Formulario para crear y editar productos (código, nombre, categoría, precio, stock y stock mínimo) con validación por campo, íconos y mensajes de error.
- Detalle del producto con registro de entradas y salidas e historial de movimientos.
- Eliminación de productos con confirmación.
- Persistencia local con SQLite (`SQLiteOpenHelper`): tablas `productos` y `movimientos` relacionadas con clave foránea, código único y transacción al registrar un movimiento.
- Recursos nativos:
  - Cámara para tomar la foto del producto.
  - Notificación cuando un producto queda con stock bajo (pide permiso en Android 13 o superior).
- Animaciones: transiciones entre pantallas, aparición de mensajes de error y alertas, cambio de color del stock y animación de los elementos de las listas.
- Interfaz con Material 3 y tema claro/oscuro.

## Arquitectura

Patrón MVVM:

```
app/src/main/java/com/example/kaijuinventory/
├── model/        Producto y Movimiento
├── data/         KaijuDbHelper (SQLite)
├── repository/   InventarioRepository
├── validation/   Reglas de validación de los formularios
├── viewmodel/    InventarioViewModel (estado con StateFlow)
├── nativo/       Cámara (fotos) y notificaciones
└── ui/           Pantallas, componentes, navegación y tema
```

## Cómo ejecutar

1. Clonar el repositorio o descargarlo con GitHub Desktop.
2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar a que Gradle sincronice las dependencias.
4. Ejecutar la configuración `app` en un emulador o en un teléfono con Android 7.0 (API 24) o superior.

Las pruebas unitarias de las validaciones se ejecutan con:

```
./gradlew test
```
