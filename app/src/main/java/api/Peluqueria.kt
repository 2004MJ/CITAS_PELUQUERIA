package com.example.citas_peluqueria.api

data class Peluqueria(
    val id: Long,
    // El truco: Ponemos un valor por defecto (= "")
    // Así, si no se lo pasamos (como en la reserva), Kotlin no se queja.
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)