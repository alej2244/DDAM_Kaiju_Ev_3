package com.example.kaijuinventory.repository

import com.example.kaijuinventory.data.InventarioDao
import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.model.TipoMovimiento
import com.example.kaijuinventory.network.IndicadoresApiService
import com.example.kaijuinventory.network.InventarioApiService
import com.example.kaijuinventory.network.MovimientoDto
import com.example.kaijuinventory.network.RetrofitClient
import com.example.kaijuinventory.network.toDto

class InventarioRepository(
    private val dao: InventarioDao,
    private val api: InventarioApiService = RetrofitClient.inventarioApi,
    private val indicadoresApi: IndicadoresApiService = RetrofitClient.indicadoresApi
) {

    suspend fun obtenerProductos(): List<Producto> {
        return try {
            val remotos = api.obtenerProductos()
            val locales = dao.obtenerProductos().associateBy { it.codigo }
            remotos.map { dto ->
                dto.toModel(fotoLocal = locales[dto.codigo]?.foto)
            }
        } catch (e: Exception) {
            dao.obtenerProductos()
        }
    }

    suspend fun existeCodigo(codigo: String, excluirId: Long): Boolean {
        return try {
            api.obtenerProductos().any { it.codigo.equals(codigo, ignoreCase = true) && it.id != excluirId }
        } catch (e: Exception) {
            dao.existeCodigo(codigo, excluirId)
        }
    }

    suspend fun guardarProducto(producto: Producto) {
        try {
            if (producto.id == 0L) {
                api.crearProducto(producto.toDto())
            } else {
                api.actualizarProducto(producto.id, producto.toDto())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Guardado seguro en Room sin que un conflicto de índice cierre la app
        try {
            if (producto.id == 0L) dao.insertarProducto(producto) else dao.actualizarProducto(producto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun eliminarProducto(id: Long) {
        try {
            api.eliminarProducto(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            dao.eliminarProducto(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun obtenerMovimientos(productoId: Long): List<Movimiento> {
        return try {
            api.obtenerMovimientos(productoId).map { it.toModel() }
        } catch (e: Exception) {
            dao.obtenerMovimientos(productoId)
        }
    }

    suspend fun registrarMovimiento(producto: Producto, tipo: TipoMovimiento, cantidad: Int): Int {
        val nuevoStock = when (tipo) {
            TipoMovimiento.ENTRADA -> producto.stock + cantidad
            TipoMovimiento.SALIDA -> producto.stock - cantidad
        }
        try {
            api.registrarMovimiento(
                MovimientoDto(
                    productoId = producto.id,
                    tipo = tipo.name,
                    cantidad = cantidad
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val movimiento = Movimiento(
                productoId = producto.id,
                tipo = tipo,
                cantidad = cantidad,
                fecha = System.currentTimeMillis()
            )
            dao.registrarMovimientoTransaccion(movimiento, nuevoStock)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return nuevoStock
    }

    suspend fun obtenerValorDolar(): Double? {
        return try {
            indicadoresApi.obtenerIndicadores().dolar.valor
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}