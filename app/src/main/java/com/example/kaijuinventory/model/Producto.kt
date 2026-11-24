package com.example.kaijuinventory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    indices = [Index(value = ["codigo"], unique = true)]
)
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    @ColumnInfo(name = "stock_minimo")
    val stockMinimo: Int,
    val foto: String? = null
) {
    val stockBajo: Boolean
        get() = stock <= stockMinimo
}