package com.example.kaijuinventory.model

data class Producto(
    val id: Long = 0,
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val stockMinimo: Int,
    val foto: String? = null
) {
    val stockBajo: Boolean
        get() = stock <= stockMinimo
}
