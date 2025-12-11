package com.example.citas_peluqueria.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query // <--- IMPORTANTE: Este es nuevo

interface ApiService {

    // 1. Peluquerías
    @GET("/api/peluquerias")
    fun obtenerPeluquerias(): Call<List<Peluqueria>>

    // 2. Servicios
    @GET("/api/servicios")
    fun obtenerServicios(): Call<List<Servicio>>

    // 3. Crear Cita
    @POST("/api/citas")
    fun crearCita(@Body cita: Cita): Call<Cita>

    // 4. Mis Reservas
    @GET("/api/citas/usuario/{uid}")
    fun obtenerCitasUsuario(@Path("uid") uid: String): Call<List<Cita>>

    // 5. Cancelar
    @DELETE("/api/citas/{id}")
    fun eliminarCita(@Path("id") id: Long): Call<Void>

    // --- NUEVO: PREGUNTAR HORAS OCUPADAS ---
    @GET("/api/citas/ocupadas")
    fun obtenerHorasOcupadas(
        @Query("fecha") fecha: String,
        @Query("peluqueriaId") peluqueriaId: Long
    ): Call<List<String>>
}