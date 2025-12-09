package com.example.citas_peluqueria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// IMPORTS DE TU API (Asegúrate que coinciden con tu paquete)
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

        // 1. Configurar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerMisReservas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 2. Cargar datos reales (Aquí ya no usamos listaPrueba)
        cargarReservasDesdeInternet()

        return view
    }

    private fun cargarReservasDesdeInternet() {
        // Llamamos a Spring Boot
        RetrofitClient.getApi().obtenerCitasUsuario(usuarioActual).enqueue(object : Callback<List<Cita>> {
            override fun onResponse(call: Call<List<Cita>>, response: Response<List<Cita>>) {
                if (response.isSuccessful) {
                    // Recibimos la lista de citas del servidor
                    val listaCitas = response.body() ?: emptyList()

                    // Convertimos a MutableList para poder borrar elementos si queremos
                    val listaMutable = listaCitas.toMutableList()

                    // --- AQUÍ CONFIGURAMOS EL ADAPTADOR SIN ERRORES ---
                    adapter = ReservasAdapter(listaMutable) { citaAborrar ->
                        // Esta es la acción al pulsar el botón cancelar
                        borrarCitaEnElServidor(citaAborrar)
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Cita>>, t: Throwable) {
                Toast.makeText(context, "Error de conexión: Verifica tu IP", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun borrarCitaEnElServidor(cita: Cita) {
        // Accedemos al nombre correctamente usando la estructura anidada
        Toast.makeText(context, "Cancelando: ${cita.servicio.nombre}", Toast.LENGTH_SHORT).show()

        // Llamada DELETE a la API
        RetrofitClient.getApi().eliminarCita(cita.id!!).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cita eliminada", Toast.LENGTH_SHORT).show()
                    // Recargamos la lista para ver que desapareció
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