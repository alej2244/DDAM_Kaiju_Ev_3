package com.example.kaijuinventory.network

import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.model.TipoMovimiento

// DTOs para Spring Boot
data class ProductoDto(
    val id: Long? = null,
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int
) {
    fun toModel(fotoLocal: String? = null) = Producto(
        id = id ?: 0L,
        codigo = codigo,
        nombre = nombre,
        categoria = categoria,
        precio = precio.toInt(),
        stock = stock,
        stockMinimo = stockMinimo,
        foto = fotoLocal
    )
}

fun Producto.toDto() = ProductoDto(
    id = if (id == 0L) null else id,
    codigo = codigo,
    nombre = nombre,
    categoria = categoria,
    precio = precio.toDouble(),
    stock = stock,
    stockMinimo = stockMinimo
)

data class MovimientoDto(
    val id: Long? = null,
    val productoId: Long,
    val tipo: String,
    val cantidad: Int,
    val motivo: String = "Movimiento móvil",
    val fecha: String? = null
) {
    fun toModel() = Movimiento(
        id = id ?: 0L,
        productoId = productoId,
        tipo = if (tipo.equals("ENTRADA", ignoreCase = true)) TipoMovimiento.ENTRADA else TipoMovimiento.SALIDA,
        cantidad = cantidad,
        fecha = System.currentTimeMillis()
    )
}

// DTOs para API Externa (Mindicador.cl - Valor del Dólar en CLP)
data class MindicadorResponse(
    val dolar: IndicadorDetalle
)

data class IndicadorDetalle(
    val codigo: String,
    val nombre: String,
    val valor: Double
)
// DTOs para Inicio de Sesión
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Long? = null,
    val username: String? = null,
    val rol: String? = null,
    val mensaje: String? = null
)