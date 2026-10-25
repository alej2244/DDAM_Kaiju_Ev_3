package com.example.kaijuinventory.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.model.TipoMovimiento

class KaijuDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT NOT NULL UNIQUE,
                nombre TEXT NOT NULL,
                categoria TEXT NOT NULL,
                precio INTEGER NOT NULL,
                stock INTEGER NOT NULL CHECK (stock >= 0),
                stock_minimo INTEGER NOT NULL,
                foto TEXT
            )
            """
        )
        db.execSQL(
            """
            CREATE TABLE movimientos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                producto_id INTEGER NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
                tipo TEXT NOT NULL,
                cantidad INTEGER NOT NULL,
                fecha INTEGER NOT NULL
            )
            """
        )
        db.execSQL("CREATE INDEX idx_movimientos_producto ON movimientos(producto_id)")

        // Catálogo ficticio para probar la app
        listOf(
            Producto(codigo = "KJ-001", nombre = "Polera Kaiju negra", categoria = "Ropa", precio = 12990, stock = 25, stockMinimo = 5),
            Producto(codigo = "KJ-002", nombre = "Taza cerámica", categoria = "Hogar", precio = 6990, stock = 3, stockMinimo = 4),
            Producto(codigo = "KJ-003", nombre = "Figura coleccionable", categoria = "Juguetes", precio = 19990, stock = 10, stockMinimo = 2)
        ).forEach { db.insert("productos", null, valores(it)) }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS movimientos")
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }

    fun obtenerProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        readableDatabase.query("productos", null, null, null, null, null, "nombre").use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerProducto(cursor))
            }
        }
        return lista
    }

    fun existeCodigo(codigo: String, excluirId: Long): Boolean {
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM productos WHERE codigo = ? AND id != ?",
            arrayOf(codigo, excluirId.toString())
        ).use { cursor ->
            cursor.moveToFirst()
            return cursor.getInt(0) > 0
        }
    }

    fun insertarProducto(producto: Producto): Long =
        writableDatabase.insert("productos", null, valores(producto))

    fun actualizarProducto(producto: Producto) {
        writableDatabase.update("productos", valores(producto), "id = ?", arrayOf(producto.id.toString()))
    }

    fun eliminarProducto(id: Long) {
        // Sus movimientos se borran por ON DELETE CASCADE
        writableDatabase.delete("productos", "id = ?", arrayOf(id.toString()))
    }

    fun obtenerMovimientos(productoId: Long): List<Movimiento> {
        val lista = mutableListOf<Movimiento>()
        readableDatabase.query(
            "movimientos", null, "producto_id = ?", arrayOf(productoId.toString()),
            null, null, "fecha DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Movimiento(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        productoId = cursor.getLong(cursor.getColumnIndexOrThrow("producto_id")),
                        tipo = TipoMovimiento.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipo"))),
                        cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                        fecha = cursor.getLong(cursor.getColumnIndexOrThrow("fecha"))
                    )
                )
            }
        }
        return lista
    }

    // Guarda el movimiento y actualiza el stock en una sola transacción
    fun registrarMovimiento(movimiento: Movimiento, nuevoStock: Int) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val valoresMovimiento = ContentValues().apply {
                put("producto_id", movimiento.productoId)
                put("tipo", movimiento.tipo.name)
                put("cantidad", movimiento.cantidad)
                put("fecha", movimiento.fecha)
            }
            db.insertOrThrow("movimientos", null, valoresMovimiento)

            val valoresStock = ContentValues().apply { put("stock", nuevoStock) }
            db.update("productos", valoresStock, "id = ?", arrayOf(movimiento.productoId.toString()))

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun valores(producto: Producto) = ContentValues().apply {
        put("codigo", producto.codigo)
        put("nombre", producto.nombre)
        put("categoria", producto.categoria)
        put("precio", producto.precio)
        put("stock", producto.stock)
        put("stock_minimo", producto.stockMinimo)
        put("foto", producto.foto)
    }

    private fun leerProducto(cursor: Cursor) = Producto(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
        codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
        categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
        precio = cursor.getInt(cursor.getColumnIndexOrThrow("precio")),
        stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
        stockMinimo = cursor.getInt(cursor.getColumnIndexOrThrow("stock_minimo")),
        foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
    )

    companion object {
        private const val NOMBRE_BD = "kaiju_inventario.db"
        private const val VERSION_BD = 1
    }
}
