package com.example.citas_peluqueria.api

// "data class" genera automáticamente getters, setters y toString()
data class Usuario(
    var id: Long? = null,  // El ? significa que puede ser nulo (al crearlo no tiene ID)
    var nombre: String,
    var email: String
)