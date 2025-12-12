package com.example.citas_peluqueria.api // O tu paquete correspondiente

data class Reserva(
    val id: Long,
    val nombreServicio: String,
    val fecha: String,
    val hora: String,
    val nombrePeluqueria: String,
    val direccion: String,
    var estado: String = "PENDIENTE"
)