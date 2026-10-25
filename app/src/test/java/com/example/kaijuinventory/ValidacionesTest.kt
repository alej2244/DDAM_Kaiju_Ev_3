package com.example.kaijuinventory

import com.example.kaijuinventory.validation.Validaciones
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun codigoValido() {
        assertNull(Validaciones.validarCodigo("KJ-010"))
    }

    @Test
    fun codigoVacioOConEspacios() {
        assertNotNull(Validaciones.validarCodigo(""))
        assertNotNull(Validaciones.validarCodigo("KJ 01"))
    }

    @Test
    fun precioDebeSerPositivo() {
        assertNotNull(Validaciones.validarPrecio("0"))
        assertNotNull(Validaciones.validarPrecio("abc"))
        assertNull(Validaciones.validarPrecio("5990"))
    }

    @Test
    fun salidaNoPuedeSuperarElStock() {
        assertNotNull(Validaciones.validarCantidad("8", esSalida = true, stockActual = 5))
        assertNull(Validaciones.validarCantidad("8", esSalida = false, stockActual = 5))
        assertNull(Validaciones.validarCantidad("5", esSalida = true, stockActual = 5))
    }
}
