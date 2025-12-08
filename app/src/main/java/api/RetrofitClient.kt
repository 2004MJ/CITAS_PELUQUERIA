package com.example.citas_peluqueria.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Usamos 'object' para que funcione como un Singleton (estático)
object RetrofitClient {

    // IP para el emulador (si usas móvil real cambia esto por tu IP local)
    private const val BASE_URL = "http://10.0.2.2:8081/"

    private var retrofit: Retrofit? = null

    fun getApi(): ApiService {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }
}