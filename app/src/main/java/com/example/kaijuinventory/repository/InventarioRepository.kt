package com.example.kaijuinventory.repository

import com.example.kaijuinventory.data.KaijuDbHelper
import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.model.TipoMovimiento

class InventarioRepository(private val db: KaijuDbHelper) {

    fun obtenerProductos(): List<Producto> = db.obtenerProductos()

    fun existeCodigo(codigo: String, excluirId: Long): Boolean = db.existeCodigo(codigo, excluirId)

    fun guardarProducto(producto: Producto) {
        if (producto.id == 0L) db.insertarProducto(producto) else db.actualizarProducto(producto)
    }

    fun eliminarProducto(id: Long) = db.eliminarProducto(id)

    fun obtenerMovimientos(productoId: Long): List<Movimiento> = db.obtenerMovimientos(productoId)

    // Devuelve el stock que queda después del movimiento
    fun registrarMovimiento(producto: Producto, tipo: TipoMovimiento, cantidad: Int): Int {
        val nuevoStock = when (tipo) {
            TipoMovimiento.ENTRADA -> producto.stock + cantidad
            TipoMovimiento.SALIDA -> producto.stock - cantidad
        }
        val movimiento = Movimiento(
            productoId = producto.id,
            tipo = tipo,
            cantidad = cantidad,
            fecha = System.currentTimeMillis()
        )
        db.registrarMovimiento(movimiento, nuevoStock)
        return nuevoStock
    }
}
