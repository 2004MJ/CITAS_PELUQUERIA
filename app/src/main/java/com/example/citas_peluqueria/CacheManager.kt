package com.example.citas_peluqueria.utils

import android.content.Context
import com.example.citas_peluqueria.api.Cita
import com.example.citas_peluqueria.api.Peluqueria
import com.example.citas_peluqueria.api.Servicio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheManager(context: Context) {

    private val prefs = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    // --- SERVICIOS ---
    fun guardarServicios(lista: List<Servicio>) {
        prefs.edit().putString("lista_servicios", gson.toJson(lista)).apply()
    }

    fun obtenerServicios(): List<Servicio>? {
        val json = prefs.getString("lista_servicios", null) ?: return null
        val type = object : TypeToken<List<Servicio>>() {}.type
        return gson.fromJson(json, type)
    }

    // --- PELUQUERÍAS (NUEVO) ---
    fun guardarPeluquerias(lista: List<Peluqueria>) {
        prefs.edit().putString("lista_peluquerias", gson.toJson(lista)).apply()
    }

    fun obtenerPeluquerias(): List<Peluqueria>? {
        val json = prefs.getString("lista_peluquerias", null) ?: return null
        val type = object : TypeToken<List<Peluqueria>>() {}.type
        return gson.fromJson(json, type)
    }
    fun guardarMisReservas(lista: List<Cita>) {
        val jsonString = gson.toJson(lista)
        prefs.edit().putString("mis_reservas_cache", jsonString).apply()
    }

    fun obtenerMisReservas(): List<Cita>? {
        val jsonString = prefs.getString("mis_reservas_cache", null) ?: return null
        val type = object : TypeToken<List<Cita>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}