package com.example.kaijuinventory.model

data class Movimiento(
    val id: Long = 0,
    val productoId: Long,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val fecha: Long
)

enum class TipoMovimiento(val texto: String) {
    ENTRADA("Entrada"),
    SALIDA("Salida")
}
