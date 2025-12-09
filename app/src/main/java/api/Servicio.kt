package com.example.citas_peluqueria.api

data class Servicio(
    val id: Long,
    // Añadimos nombre y precio con valores por defecto para no romper el POST
    val nombre: String = "",
    val precio: Double = 0.0
)