package com.example.citas_peluqueria.api // O tu paquete correspondiente

data class Reserva(
    val id: Long,
    val nombreServicio: String, // Ej: "Corte de Pelo"
    val fecha: String,          // Ej: "12/12/2023"
    val hora: String,           // Ej: "16:30"
    val nombrePeluqueria: String,
    val direccion: String,
    var estado: String = "PENDIENTE" // Para saber si está cancelada o activa
)