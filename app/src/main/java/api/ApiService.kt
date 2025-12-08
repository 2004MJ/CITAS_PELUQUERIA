package com.example.citas_peluqueria.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    // Tus otras funciones de Login/Registro irían aquí...

    // --- AÑADE ESTA LÍNEA ---
    @GET("/api/peluquerias")
    fun obtenerPeluquerias(): Call<List<Peluqueria>>
}