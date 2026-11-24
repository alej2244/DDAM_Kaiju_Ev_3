package com.example.kaijuinventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto

@Dao
interface InventarioDao {

    @Query("SELECT * FROM productos ORDER BY nombre")
    suspend fun obtenerProductos(): List<Producto>

    @Query("SELECT COUNT(*) > 0 FROM productos WHERE codigo = :codigo AND id != :excluirId")
    suspend fun existeCodigo(codigo: String, excluirId: Long): Boolean

    @Insert
    suspend fun insertarProducto(producto: Producto): Long

    @Update
    suspend fun actualizarProducto(producto: Producto)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarProducto(id: Long)

    @Query("SELECT * FROM movimientos WHERE producto_id = :productoId ORDER BY fecha DESC")
    suspend fun obtenerMovimientos(productoId: Long): List<Movimiento>

    @Insert
    suspend fun insertarMovimiento(movimiento: Movimiento)

    @Query("UPDATE productos SET stock = :nuevoStock WHERE id = :productoId")
    suspend fun actualizarStock(productoId: Long, nuevoStock: Int)

    @Transaction
    suspend fun registrarMovimientoTransaccion(movimiento: Movimiento, nuevoStock: Int) {
        insertarMovimiento(movimiento)
        actualizarStock(movimiento.productoId, nuevoStock)
    }
}