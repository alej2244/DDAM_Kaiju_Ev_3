package com.example.kaijuinventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kaijuinventory.model.Movimiento
import com.example.kaijuinventory.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Producto::class, Movimiento::class], version = 1, exportSchema = false)
abstract class KaijuDatabase : RoomDatabase() {

    abstract fun inventarioDao(): InventarioDao

    companion object {
        @Volatile
        private var INSTANCE: KaijuDatabase? = null

        fun getDatabase(context: Context): KaijuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KaijuDatabase::class.java,
                    "kaiju_inventario.db"
                )
                    .addCallback(SeedDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.inventarioDao()
                    dao.insertarProducto(Producto(codigo = "KJ-001", nombre = "Polera Kaiju negra", categoria = "Ropa", precio = 12990, stock = 25, stockMinimo = 5))
                    dao.insertarProducto(Producto(codigo = "KJ-002", nombre = "Taza cerámica", categoria = "Hogar", precio = 6990, stock = 3, stockMinimo = 4))
                    dao.insertarProducto(Producto(codigo = "KJ-003", nombre = "Figura coleccionable", categoria = "Juguetes", precio = 19990, stock = 10, stockMinimo = 2))
                }
            }
        }
    }
}