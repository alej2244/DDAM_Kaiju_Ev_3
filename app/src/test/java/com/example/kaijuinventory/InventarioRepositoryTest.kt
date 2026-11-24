package com.example.kaijuinventory

import com.example.kaijuinventory.data.InventarioDao
import com.example.kaijuinventory.model.Producto
import com.example.kaijuinventory.model.TipoMovimiento
import com.example.kaijuinventory.network.IndicadorDetalle
import com.example.kaijuinventory.network.IndicadoresApiService
import com.example.kaijuinventory.network.InventarioApiService
import com.example.kaijuinventory.network.MindicadorResponse
import com.example.kaijuinventory.network.MovimientoDto
import com.example.kaijuinventory.network.ProductoDto
import com.example.kaijuinventory.repository.InventarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InventarioRepositoryTest {

    private lateinit var dao: InventarioDao
    private lateinit var api: InventarioApiService
    private lateinit var indicadoresApi: IndicadoresApiService
    private lateinit var repository: InventarioRepository

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        api = mockk(relaxed = true)
        indicadoresApi = mockk(relaxed = true)
        repository = InventarioRepository(dao, api, indicadoresApi)
    }

    @Test
    fun `obtenerProductos retorna lista desde el microservicio Spring Boot`() = runTest {
        val productosRemotos = listOf(
            ProductoDto(1L, "KJ-001", "Polera Kaiju", "Ropa", 12990.0, 25, 5)
        )
        coEvery { api.obtenerProductos() } returns productosRemotos
        coEvery { dao.obtenerProductos() } returns emptyList()

        val resultado = repository.obtenerProductos()

        assertEquals(1, resultado.size)
        assertEquals("Polera Kaiju", resultado[0].nombre)
        assertEquals(12990, resultado[0].precio)
    }

    @Test
    fun `obtenerProductos usa respaldo local Room si el servidor falla`() = runTest {
        val productosLocales = listOf(
            Producto(1L, "KJ-001", "Polera Offline", "Ropa", 12990, 10, 5)
        )
        coEvery { api.obtenerProductos() } throws RuntimeException("Sin conexión")
        coEvery { dao.obtenerProductos() } returns productosLocales

        val resultado = repository.obtenerProductos()

        assertEquals(1, resultado.size)
        assertEquals("Polera Offline", resultado[0].nombre)
    }

    @Test
    fun `registrarMovimiento suma correctamente el stock en una entrada`() = runTest {
        val producto = Producto(1L, "KJ-001", "Polera", "Ropa", 12990, 10, 5)
        coEvery { api.registrarMovimiento(any()) } returns MovimientoDto(1L, 1L, "ENTRADA", 5)

        val nuevoStock = repository.registrarMovimiento(producto, TipoMovimiento.ENTRADA, 5)

        assertEquals(15, nuevoStock)
        coVerify { dao.registrarMovimientoTransaccion(any(), 15) }
    }

    @Test
    fun `obtenerValorDolar retorna el valor desde la API externa`() = runTest {
        val respuestaApi = MindicadorResponse(
            dolar = IndicadorDetalle("dolar", "Dólar observado", 950.5)
        )
        coEvery { indicadoresApi.obtenerIndicadores() } returns respuestaApi

        val dolar = repository.obtenerValorDolar()

        assertEquals(950.5, dolar)
    }
}