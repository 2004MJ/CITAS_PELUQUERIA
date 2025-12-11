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
import com.example.citas_peluqueria.utils.CacheManager
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

    // Cache Manager
    private lateinit var cacheManager: CacheManager

    // GENERAMOS LAS HORAS (De 08:00 a 20:00)
    private val todasLasHoras = (8..20).map { String.format("%02d:00", it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva, container, false)

        // Inicializamos CacheManager
        cacheManager = CacheManager(requireContext())

        // 1. Vincular vistas
        spinnerPeluquerias = view.findViewById(R.id.spinner_peluquerias)
        spinnerServicios = view.findViewById(R.id.spinner_servicios)
        calendarView = view.findViewById(R.id.calendarView)
        recyclerHoras = view.findViewById(R.id.recyclerHoras)
        btnConfirmar = view.findViewById(R.id.button_confirmar_reserva)
        tvTituloHoras = view.findViewById(R.id.tvSeleccionaHora)

        // 2. Configurar la rejilla de horas
        recyclerHoras.layoutManager = GridLayoutManager(context, 4)

        // 3. Cargar Spinners (CON CACHÉ)
        cargarPeluquerias()
        cargarServicios()

        // 4. Lógica del Calendario
        configurarCalendario()

        // 5. Botón Confirmar
        btnConfirmar.setOnClickListener { guardarReservaReal() }

        return view
    }

    // ----------------------------------------------------------------
    // LÓGICA DE CACHÉ + RED (PELUQUERÍAS)
    // ----------------------------------------------------------------
    private fun cargarPeluquerias() {
        val peluqueriasCache = cacheManager.obtenerPeluquerias()
        if (!peluqueriasCache.isNullOrEmpty()) {
            listaPeluqueriasReal = peluqueriasCache
            actualizarSpinnerPeluquerias()
        }

        RetrofitClient.getApi().obtenerPeluquerias().enqueue(object : Callback<List<Peluqueria>> {
            override fun onResponse(call: Call<List<Peluqueria>>, response: Response<List<Peluqueria>>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaFresca = response.body()!!
                    listaPeluqueriasReal = listaFresca
                    actualizarSpinnerPeluquerias()
                    cacheManager.guardarPeluquerias(listaFresca)
                }
            }
            override fun onFailure(call: Call<List<Peluqueria>>, t: Throwable) {
                if (listaPeluqueriasReal.isEmpty()) {
                    Toast.makeText(context, "Error cargando peluquerías", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun actualizarSpinnerPeluquerias() {
        if (context == null) return
        val nombres = listaPeluqueriasReal.map { it.nombre }
        spinnerPeluquerias.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
    }

    // ----------------------------------------------------------------
    // LÓGICA DE CACHÉ + RED (SERVICIOS)
    // ----------------------------------------------------------------
    private fun cargarServicios() {
        val serviciosCache = cacheManager.obtenerServicios()
        if (!serviciosCache.isNullOrEmpty()) {
            listaServiciosReal = serviciosCache
            actualizarSpinnerServicios()
        }

        RetrofitClient.getApi().obtenerServicios().enqueue(object : Callback<List<Servicio>> {
            override fun onResponse(call: Call<List<Servicio>>, response: Response<List<Servicio>>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaFresca = response.body()!!
                    listaServiciosReal = listaFresca
                    actualizarSpinnerServicios()
                    cacheManager.guardarServicios(listaFresca)
                }
            }
            override fun onFailure(call: Call<List<Servicio>>, t: Throwable) {
                if (listaServiciosReal.isEmpty()) {
                    Toast.makeText(context, "Error cargando servicios", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun actualizarSpinnerServicios() {
        if (context == null) return
        val nombres = listaServiciosReal.map { "${it.nombre} (${it.precio}€)" }
        spinnerServicios.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
    }

    // ----------------------------------------------------------------
    // LÓGICA DE CALENDARIO
    // ----------------------------------------------------------------
    private fun configurarCalendario() {
        calendarView.minDate = System.currentTimeMillis() - 1000
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val diaSemana = calendar.get(Calendar.DAY_OF_WEEK)

            if (diaSemana == Calendar.SUNDAY) {
                Toast.makeText(context, "🚫 Domingos cerrado", Toast.LENGTH_SHORT).show()
                ocultarHoras()
                return@setOnDateChangeListener
            }

            fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            cargarHorasDisponibles(fechaSeleccionada)
        }
    }

    private fun cargarHorasDisponibles(fecha: String) {
        val peluqueriaId = if (listaPeluqueriasReal.isNotEmpty()) {
            listaPeluqueriasReal[spinnerPeluquerias.selectedItemPosition].id
        } else { 1L }

        tvTituloHoras.text = "Consultando..."
        tvTituloHoras.visibility = View.VISIBLE
        recyclerHoras.visibility = View.GONE
        btnConfirmar.isEnabled = false

        RetrofitClient.getApi().obtenerHorasOcupadas(fecha, peluqueriaId).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val horasOcupadas = response.body() ?: emptyList()
                    tvTituloHoras.text = "3. Elige una Hora ($fecha)"
                    recyclerHoras.visibility = View.VISIBLE
                    recyclerHoras.adapter = HorasAdapter(todasLasHoras, horasOcupadas) { horaClick ->
                        horaSeleccionada = horaClick
                        btnConfirmar.isEnabled = true
                        btnConfirmar.text = "CONFIRMAR ($fecha - $horaClick)"
                    }
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

    // ----------------------------------------------------------------
    // NUEVA LÓGICA: GUARDAR RESERVA Y ACTUALIZAR CACHÉ DE CITAS
    // ----------------------------------------------------------------
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
                    val nuevaCita = response.body()

                    // --- AQUÍ ESTÁ LA MAGIA DE LA CACHÉ ---
                    if (nuevaCita != null) {
                        // 1. Obtenemos la lista que ya teníamos guardada (o creamos una vacía)
                        val listaActual = cacheManager.obtenerMisReservas()?.toMutableList() ?: mutableListOf()

                        // 2. Añadimos la nueva cita
                        listaActual.add(nuevaCita)

                        // 3. Guardamos la lista actualizada en el móvil
                        cacheManager.guardarMisReservas(listaActual)
                    }
                    // --------------------------------------

                    Toast.makeText(context, "¡Reserva Confirmada!", Toast.LENGTH_LONG).show()

                    // Al ir a MisReservasFragment, la cita ya estará ahí gracias al paso anterior
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