package com.example.kaijuinventory.validation

// Cada función devuelve el mensaje de error o null si el valor es válido
object Validaciones {

    fun validarCodigo(codigo: String): String? = when {
        codigo.isBlank() -> "El código es obligatorio"
        !codigo.matches(Regex("[A-Za-z0-9-]{3,10}")) -> "Usa de 3 a 10 letras, números o guiones"
        else -> null
    }

    fun validarNombre(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre es obligatorio"
        nombre.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    fun validarCategoria(categoria: String): String? =
        if (categoria.isBlank()) "La categoría es obligatoria" else null

    fun validarPrecio(precio: String): String? {
        val valor = precio.toIntOrNull()
        return when {
            valor == null -> "Ingresa un precio válido"
            valor <= 0 -> "El precio debe ser mayor a 0"
            else -> null
        }
    }

    fun validarStock(stock: String): String? {
        val valor = stock.toIntOrNull()
        return when {
            valor == null -> "Ingresa un número entero"
            valor < 0 -> "No puede ser negativo"
            else -> null
        }
    }

    fun validarCantidad(cantidad: String, esSalida: Boolean, stockActual: Int): String? {
        val valor = cantidad.toIntOrNull()
        return when {
            valor == null -> "Ingresa una cantidad"
            valor <= 0 -> "La cantidad debe ser mayor a 0"
            esSalida && valor > stockActual -> "Stock insuficiente (disponible: $stockActual)"
            else -> null
        }
    }
}
