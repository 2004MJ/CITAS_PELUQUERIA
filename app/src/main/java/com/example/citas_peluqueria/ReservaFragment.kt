package com.example.citas_peluqueria

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.Calendar

// Imports de tus clases API
import com.example.citas_peluqueria.api.Cita
import com.example.citas_peluqueria.api.Peluqueria
import com.example.citas_peluqueria.api.Servicio
import com.example.citas_peluqueria.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservaFragment : Fragment() {

    private var fechaSeleccionada = ""
    private var horaSeleccionada = ""

    // Listas para guardar los datos REALES
    private var listaPeluqueriasReal: List<Peluqueria> = emptyList()
    private var listaServiciosReal: List<Servicio> = emptyList()

    private lateinit var spinnerPeluquerias: Spinner
    private lateinit var spinnerServicios: Spinner
    private lateinit var btnFecha: Button
    private lateinit var btnHora: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva, container, false)

        spinnerPeluquerias = view.findViewById(R.id.spinner_peluquerias)
        spinnerServicios = view.findViewById(R.id.spinner_servicios)
        btnFecha = view.findViewById(R.id.button_fecha)
        btnHora = view.findViewById(R.id.button_hora)
        val btnConfirmar: Button = view.findViewById(R.id.button_confirmar_reserva)

        // Cargar datos
        cargarPeluqueriasApi()
        cargarServiciosApi()

        // Configurar botones
        btnFecha.setOnClickListener { mostrarCalendario() }
        btnHora.setOnClickListener { mostrarReloj() }
        btnConfirmar.setOnClickListener { guardarReservaReal() }

        return view
    }

    // --- CARGAR PELUQUERÍAS ---
    private fun cargarPeluqueriasApi() {
        RetrofitClient.getApi().obtenerPeluquerias().enqueue(object : Callback<List<Peluqueria>> {
            override fun onResponse(call: Call<List<Peluqueria>>, response: Response<List<Peluqueria>>) {
                if (response.isSuccessful) {
                    listaPeluqueriasReal = response.body() ?: emptyList()
                    val nombres = listaPeluqueriasReal.map { it.nombre }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                    spinnerPeluquerias.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<Peluqueria>>, t: Throwable) {
                Toast.makeText(context, "Error cargando peluquerías", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- CARGAR SERVICIOS (AQUÍ ESTABA EL ERROR, YA CORREGIDO) ---
    private fun cargarServiciosApi() {
        // Fíjate que ahora el paréntesis de cierre ')' está al final del bloque, no después de <List<Servicio>>
        RetrofitClient.getApi().obtenerServicios().enqueue(object : Callback<List<Servicio>> {
            override fun onResponse(call: Call<List<Servicio>>, response: Response<List<Servicio>>) {
                if (response.isSuccessful) {
                    listaServiciosReal = response.body() ?: emptyList()
                    val nombres = listaServiciosReal.map { "${it.nombre} (${it.precio}€)" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                    spinnerServicios.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<Servicio>>, t: Throwable) {
                Toast.makeText(context, "Error cargando servicios", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarCalendario() {
        val calendario = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(), { _, anio, mes, dia ->
            val checkCalendar = Calendar.getInstance()
            checkCalendar.set(anio, mes, dia)
            val diaSemana = checkCalendar.get(Calendar.DAY_OF_WEEK)

            if (diaSemana == Calendar.SUNDAY) {
                Toast.makeText(context, "🚫 Los domingos estamos cerrados", Toast.LENGTH_LONG).show()
                fechaSeleccionada = ""
                btnFecha.text = "Seleccionar Fecha"
            } else {
                val fechaFormateada = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
                fechaSeleccionada = fechaFormateada
                btnFecha.text = "Fecha: $fechaFormateada"
            }
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH))

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun mostrarReloj() {
        val calendario = Calendar.getInstance()
        val timePicker = TimePickerDialog(requireContext(), { _, h, m ->
            if (h < 8 || h >= 21) {
                Toast.makeText(context, "🚫 Horario de atención: 08:00 a 21:00", Toast.LENGTH_LONG).show()
                horaSeleccionada = ""
                btnHora.text = "Seleccionar Hora"
            } else {
                val horaFormateada = String.format("%02d:%02d", h, m)
                horaSeleccionada = horaFormateada
                btnHora.text = "Hora: $horaFormateada"
            }
        }, calendario.get(Calendar.HOUR_OF_DAY), 0, true)

        timePicker.show()
    }

    private fun guardarReservaReal() {
        if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
            Toast.makeText(context, "Por favor elige fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }
        if (listaPeluqueriasReal.isEmpty() || listaServiciosReal.isEmpty()) {
            Toast.makeText(context, "Cargando datos... intenta de nuevo", Toast.LENGTH_SHORT).show()
            return
        }

        // Recuperamos los objetos reales
        val peluqueriaElegida = listaPeluqueriasReal[spinnerPeluquerias.selectedItemPosition]
        val servicioElegido = listaServiciosReal[spinnerServicios.selectedItemPosition]

        val nuevaCita = Cita(
            clienteUid = "usuario_app",
            fecha = fechaSeleccionada,
            hora = horaSeleccionada,
            peluqueria = peluqueriaElegida,
            servicio = servicioElegido
        )

        RetrofitClient.getApi().crearCita(nuevaCita).enqueue(object : Callback<Cita> {
            override fun onResponse(call: Call<Cita>, response: Response<Cita>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "¡Reserva Confirmada!", Toast.LENGTH_LONG).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MisReservasFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "Error servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Cita>, t: Throwable) {
                Toast.makeText(context, "Fallo conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}