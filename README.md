# Kaiju Inventario — Aplicación Móvil y Microservicios en AWS

Aplicación móvil Android desarrollada en **Kotlin y Jetpack Compose (Material 3)** para la gestión y trazabilidad en tiempo real del inventario de la organización **Kaiju**. Integra un microservicio REST propio en **Java + Spring Boot** desplegado en **AWS EC2**, consumo de API externa vía **Retrofit**, respaldo local con **Room (SQLite)**, pruebas unitarias con **JUnit 4 / MockK** y generación de **APK firmado**.

**Integrante:** Alejandro Benjamín Naranjo Palma · Sección DSY1105 · Grupo 13

---

## 1. Funcionalidades Principales
* **Autenticación de personal autorizado:** Inicio de sesión validado contra microservicio Spring Boot (`POST /api/inventario/login`).
* **Catálogo CRUD en tiempo real:** Creación, lectura, edición y eliminación de productos sincronizados con la nube AWS, con formulario validado por campo.
* **Control de movimientos y trazabilidad:** Registro de entradas y salidas que actualiza automáticamente el stock disponible y conserva el historial por producto.
* **Alertas de stock bajo y recursos nativos:** Notificaciones automáticas cuando un producto alcanza su stock mínimo y captura fotográfica con la cámara del dispositivo.
* **Conversión de moneda en tiempo real (API Externa):** Consulta al valor del dólar observado desde `mindicador.cl` para mostrar precios referenciales en USD.
* **Persistencia híbrida (Online / Offline):** Sincronización remota con Spring Boot y respaldo local mediante **Room (`KaijuDatabase`)** para consultas sin conexión.

---

## 2. Arquitectura del Proyecto (MVVM + Microservicios)

```text
app/src/main/java/com/example/kaijuinventory/
├── data/          # Room (KaijuDatabase, InventarioDao, Entities)
├── model/         # Modelos de dominio y DTOs (ApiModels, Producto, Movimiento)
├── nativo/        # Cámara y gestor de notificaciones de stock bajo
├── network/       # RetrofitClient, InventarioApiService, IndicadoresApiService
├── repository/    # InventarioRepository (orquestador entre API AWS y Room)
├── ui/            # Pantallas en Jetpack Compose (Material 3), navegación y tema
├── validation/    # Reglas centralizadas de validación de formularios
└── viewmodel/     # InventarioViewModel (gestión de estado reactivo con StateFlow)
