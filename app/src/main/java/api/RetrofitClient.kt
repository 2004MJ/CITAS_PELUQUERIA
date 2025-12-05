package com.example.citas_peluqueria.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // IP del emulador (cámbiala si usas móvil físico)
    private const val BASE_URL = "http://10.0.2.2:8081/"

    // "by lazy" significa que solo se crea la primera vez que lo llamas (ahorra memoria)
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}