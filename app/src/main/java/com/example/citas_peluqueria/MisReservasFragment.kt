package com.example.citas_peluqueria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// IMPORTS DE TU API
import com.example.citas_peluqueria.api.Cita
import com.example.citas_peluqueria.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisReservasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservasAdapter

    // IMPORTANTE: Este usuario debe coincidir con el que usas al guardar la reserva
    private val usuarioActual = "usuario_app"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_reservas, container, false)

        // 1. Configurar RecyclerView (La lista)
        recyclerView = view.findViewById(R.id.recyclerMisReservas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 2. CONFIGURAR EL BOTÓN FLOTANTE (+) PARA AÑADIR CITA
        val fab: FloatingActionButton = view.findViewById(R.id.fabNuevaReserva)
        fab.setOnClickListener {
            // Al pulsar el +, navegamos al formulario de reserva
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ReservaFragment())
                .addToBackStack(null) // Esto permite volver atrás con el botón del móvil
                .commit()
        }

        // 3. Cargar datos reales desde el servidor
        cargarReservasDesdeInternet()

        return view
    }

    private fun cargarReservasDesdeInternet() {
        // Llamamos a Spring Boot pidiendo las citas de "usuario_app"
        RetrofitClient.getApi().obtenerCitasUsuario(usuarioActual).enqueue(object : Callback<List<Cita>> {
            override fun onResponse(call: Call<List<Cita>>, response: Response<List<Cita>>) {
                if (response.isSuccessful) {
                    // Recibimos la lista de citas del servidor
                    val listaCitas = response.body() ?: emptyList()

                    // -----------------------------------------------------------
                    // 👇 AQUÍ ESTÁ EL CAMBIO: ORDENAR POR FECHA Y LUEGO HORA 👇
                    // -----------------------------------------------------------
                    val listaOrdenada = listaCitas.sortedWith(compareBy(
                        { it.fecha }, // Primero mira la fecha (ej: 2025-12-25)
                        { it.hora }   // Si coincide fecha, mira la hora (ej: 10:00)
                    ))
                    // -----------------------------------------------------------

                    // Convertimos la lista YA ORDENADA a MutableList
                    val listaMutable = listaOrdenada.toMutableList()

                    // Configuramos el adaptador
                    adapter = ReservasAdapter(listaMutable) { citaAborrar ->
                        // Esta es la acción al pulsar el botón "Cancelar" de una tarjeta
                        borrarCitaEnElServidor(citaAborrar)
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Cita>>, t: Throwable) {
                Toast.makeText(context, "Error de conexión: Verifica que el servidor está encendido", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun borrarCitaEnElServidor(cita: Cita) {
        // Mostramos aviso visual
        Toast.makeText(context, "Cancelando: ${cita.servicio.nombre}", Toast.LENGTH_SHORT).show()

        // Llamada DELETE a la API usando el ID de la cita
        RetrofitClient.getApi().eliminarCita(cita.id!!).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cita eliminada correctamente", Toast.LENGTH_SHORT).show()
                    // Recargamos la lista para que desaparezca la cita borrada
                    cargarReservasDesdeInternet()
                } else {
                    Toast.makeText(context, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Fallo de red al borrar", Toast.LENGTH_SHORT).show()
            }
        })
    }
}