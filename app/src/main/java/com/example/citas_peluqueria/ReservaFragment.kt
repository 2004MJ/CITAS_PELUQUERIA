package com.example.citas_peluqueria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

import com.example.citas_peluqueria.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservaFragment : Fragment() {

    // Variables de datos
    private var fechaSeleccionada = ""
    private var horaSeleccionada = ""
    private var listaPeluqueriasReal: List<Peluqueria> = emptyList()
    private var listaServiciosReal: List<Servicio> = emptyList()

    // Vistas
    private lateinit var spinnerPeluquerias: Spinner
    private lateinit var spinnerServicios: Spinner
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerHoras: RecyclerView
    private lateinit var btnConfirmar: Button
    private lateinit var tvTituloHoras: TextView

    // GENERAMOS LAS HORAS (De 08:00 a 20:00)
    // Esto crea una lista automática: ["08:00", "09:00", ... "20:00"]
    private val todasLasHoras = (8..20).map { String.format("%02d:00", it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva, container, false)

        // 1. Vincular vistas del XML Nuevo
        spinnerPeluquerias = view.findViewById(R.id.spinner_peluquerias)
        spinnerServicios = view.findViewById(R.id.spinner_servicios)
        calendarView = view.findViewById(R.id.calendarView)
        recyclerHoras = view.findViewById(R.id.recyclerHoras)
        btnConfirmar = view.findViewById(R.id.button_confirmar_reserva)
        tvTituloHoras = view.findViewById(R.id.tvSeleccionaHora)

        // 2. Configurar la rejilla de horas (4 columnas)
        recyclerHoras.layoutManager = GridLayoutManager(context, 4)

        // 3. Cargar Spinners
        cargarPeluqueriasApi()
        cargarServiciosApi()

        // 4. Lógica del Calendario Grande
        configurarCalendario()

        // 5. Botón Confirmar
        btnConfirmar.setOnClickListener { guardarReservaReal() }

        return view
    }

    private fun configurarCalendario() {
        // Bloquear fechas pasadas
        calendarView.minDate = System.currentTimeMillis() - 1000

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // A. Verificamos si es Domingo
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val diaSemana = calendar.get(Calendar.DAY_OF_WEEK)

            if (diaSemana == Calendar.SUNDAY) {
                Toast.makeText(context, "🚫 Domingos cerrado", Toast.LENGTH_SHORT).show()
                ocultarHoras() // Ocultamos la rejilla
                return@setOnDateChangeListener
            }

            // B. Guardamos la fecha (Formato SQL: YYYY-MM-DD)
            fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            // C. Pedimos al servidor las horas ocupadas de ese día
            cargarHorasDisponibles(fechaSeleccionada)
        }
    }

    private fun cargarHorasDisponibles(fecha: String) {
        // Obtenemos el ID de la peluquería seleccionada (o 1 por defecto)
        val peluqueriaId = if (listaPeluqueriasReal.isNotEmpty()) {
            listaPeluqueriasReal[spinnerPeluquerias.selectedItemPosition].id
        } else {
            1L
        }

        // Mostramos mensaje de carga
        tvTituloHoras.text = "Consultando disponibilidad..."
        tvTituloHoras.visibility = View.VISIBLE
        recyclerHoras.visibility = View.GONE
        btnConfirmar.isEnabled = false

        // Llamada al Backend (Eclipse)
        RetrofitClient.getApi().obtenerHorasOcupadas(fecha, peluqueriaId).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val horasOcupadas = response.body() ?: emptyList()

                    // ¡ÉXITO! Mostramos la rejilla
                    tvTituloHoras.text = "3. Elige una Hora Disponible ($fecha)"
                    recyclerHoras.visibility = View.VISIBLE

                    // Configuramos el adaptador (HorasAdapter)
                    recyclerHoras.adapter = HorasAdapter(todasLasHoras, horasOcupadas) { horaClick ->
                        // Al hacer click en una hora libre:
                        horaSeleccionada = horaClick
                        btnConfirmar.isEnabled = true
                        btnConfirmar.text = "CONFIRMAR ($fecha - $horaClick)"
                    }
                } else {
                    tvTituloHoras.text = "Error al consultar horario"
                }
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun ocultarHoras() {
        recyclerHoras.visibility = View.GONE
        tvTituloHoras.visibility = View.GONE
        btnConfirmar.isEnabled = false
        fechaSeleccionada = ""
    }

    // --- MÉTODOS DE CARGA DE SPINNERS ---
    private fun cargarPeluqueriasApi() {
        RetrofitClient.getApi().obtenerPeluquerias().enqueue(object : Callback<List<Peluqueria>> {
            override fun onResponse(call: Call<List<Peluqueria>>, response: Response<List<Peluqueria>>) {
                if (response.isSuccessful) {
                    listaPeluqueriasReal = response.body() ?: emptyList()
                    val nombres = listaPeluqueriasReal.map { it.nombre }
                    spinnerPeluquerias.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                }
            }
            override fun onFailure(call: Call<List<Peluqueria>>, t: Throwable) {
                Toast.makeText(context, "Error cargando peluquerías", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarServiciosApi() {
        RetrofitClient.getApi().obtenerServicios().enqueue(object : Callback<List<Servicio>> {
            override fun onResponse(call: Call<List<Servicio>>, response: Response<List<Servicio>>) {
                if (response.isSuccessful) {
                    listaServiciosReal = response.body() ?: emptyList()
                    val nombres = listaServiciosReal.map { "${it.nombre} (${it.precio}€)" }
                    spinnerServicios.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                }
            }
            override fun onFailure(call: Call<List<Servicio>>, t: Throwable) {
                Toast.makeText(context, "Error cargando servicios", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarReservaReal() {
        if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) return

        val peluqueria = listaPeluqueriasReal[spinnerPeluquerias.selectedItemPosition]
        val servicio = listaServiciosReal[spinnerServicios.selectedItemPosition]

        val cita = Cita(
            clienteUid = "usuario_app",
            fecha = fechaSeleccionada,
            hora = horaSeleccionada,
            peluqueria = peluqueria,
            servicio = servicio
        )

        RetrofitClient.getApi().crearCita(cita).enqueue(object : Callback<Cita> {
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
                Toast.makeText(context, "Fallo conexión", Toast.LENGTH_LONG).show()
            }
        })
    }
}