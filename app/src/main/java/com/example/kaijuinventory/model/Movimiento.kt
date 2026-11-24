package com.example.kaijuinventory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["producto_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["producto_id"])]
)
data class Movimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "producto_id")
    val productoId: Long,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val fecha: Long
)

enum class TipoMovimiento(val texto: String) {
    ENTRADA("Entrada"),
    SALIDA("Salida")
}