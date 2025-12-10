package com.example.citas_peluqueria.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path // <--- Importante: Necesario para filtrar por usuario

interface ApiService {

    // 1. Para el Mapa (Obtener peluquerías)
    @GET("/api/peluquerias")
    fun obtenerPeluquerias(): Call<List<Peluqueria>>

    // 2. Para Reservar (Crear una cita nueva)
    @POST("/api/citas")
    fun crearCita(@Body cita: Cita): Call<Cita>

    // 3. Para Mis Reservas (Leer SOLO las citas de un usuario específico)
    // La parte {uid} se sustituye por el texto que le pases a la función
    @GET("/api/citas/usuario/{uid}")
    fun obtenerCitasUsuario(@Path("uid") uid: String): Call<List<Cita>>
    // --- AÑADE ESTO PARA QUE FUNCIONE EL BOTÓN CANCELAR ---

    @GET("/api/servicios")
    fun obtenerServicios(): Call<List<Servicio>>
    @DELETE("/api/citas/{id}")
    fun eliminarCita(@Path("id") id: Long): Call<Void>
}