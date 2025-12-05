package com.example.citas_peluqueria.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("/usuarios")
    fun obtenerUsuarios(): Call<List<Usuario>>

    @POST("/usuarios")
    fun guardarUsuario(@Body usuario: Usuario): Call<Usuario>
}