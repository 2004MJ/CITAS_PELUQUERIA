package com.example.citas_peluqueria.api

data class Cita(
    // AÑADE ESTA LÍNEA AL PRINCIPIO:
    val id: Long? = null,

    val clienteUid: String,
    val fecha: String,
    val hora: String,
    val peluqueria: Peluqueria,
    val servicio: Servicio
)