package com.example.kaijuinventory.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface IndicadoresApiService {
    @GET("api")
    suspend fun obtenerIndicadores(): MindicadorResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://34.193.175.75:8080/"
    private const val BASE_URL_EXTERNA = "https://mindicador.cl/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val inventarioApi: InventarioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InventarioApiService::class.java)
    }

    val indicadoresApi: IndicadoresApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_EXTERNA)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IndicadoresApiService::class.java)
    }
}