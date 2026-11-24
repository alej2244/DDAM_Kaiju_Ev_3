package com.example.kaijuinventory.network

import retrofit2.Response
import retrofit2.http.*

interface InventarioApiService {

    @POST("api/inventario/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/inventario/productos")
    suspend fun obtenerProductos(): List<ProductoDto>

    @POST("api/inventario/productos")
    suspend fun crearProducto(@Body producto: ProductoDto): ProductoDto

    @PUT("api/inventario/productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Long, @Body producto: ProductoDto): ProductoDto

    @DELETE("api/inventario/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>

    @GET("api/inventario/productos/{id}/movimientos")
    suspend fun obtenerMovimientos(@Path("id") productoId: Long): List<MovimientoDto>

    @POST("api/inventario/movimientos")
    suspend fun registrarMovimiento(@Body movimiento: MovimientoDto): MovimientoDto
}