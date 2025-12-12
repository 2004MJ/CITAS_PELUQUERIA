package com.example.citas_peluqueria

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.citas_peluqueria.api.Cita
import com.example.citas_peluqueria.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0
    private var lastShakeTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        try {
            // Inicializar SensorManager
            sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPedirCita = view.findViewById(R.id.btnPedirCita) as Button
        btnPedirCita.setOnClickListener { irAReservar() }

        val cardProximaCita = view.findViewById(R.id.cardProximaCita) as View
        val tvDetalle = view.findViewById(R.id.tvDetalleCitaHome) as TextView

        cardProximaCita.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MisReservasFragment()) // Asegúrate que este Fragment existe
                .addToBackStack(null)
                .commit()
        }

        cargarProximaCita(tvDetalle)
    }

    private fun cargarProximaCita(textView: TextView) {
        val currentUserId = "usuario_app" // OBTENER DE SESSION/PREFS

        // IMPORTANTE: Ajusta 'RetrofitClient.instance.api' según como tengas tu cliente
        val call = RetrofitClient.getApi().obtenerCitasUsuario(currentUserId) // ESTO FUNCIONARÁ

        call.enqueue(object : Callback<List<Cita>> {
            override fun onResponse(call: Call<List<Cita>>, response: Response<List<Cita>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (!lista.isNullOrEmpty()) {
                        // Lógica para encontrar la cita futura más cercana
                        val proxima = obtenerCitaMasCercana(lista)

                        if (proxima != null) {
                            textView.text = "Próxima cita: ${proxima.fecha} a las ${proxima.hora}"
                        } else {
                            textView.text = "No tienes citas futuras."
                        }
                    } else {
                        textView.text = "Sin reservas activas."
                    }
                } else {
                    textView.text = "Error al cargar."
                }
            }

            override fun onFailure(call: Call<List<Cita>>, t: Throwable) {
                textView.text = "Fallo de conexión."
            }
        })
    }

    // Función auxiliar pura para filtrar la fecha
    private fun obtenerCitaMasCercana(citas: List<Cita>): Cita? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val ahora = Date()

        return citas.filter {
            try {
                val fechaCita = sdf.parse("${it.fecha} ${it.hora}")
                fechaCita != null && fechaCita.after(ahora)
            } catch (e: Exception) { false }
        }.minByOrNull {
            sdf.parse("${it.fecha} ${it.hora}")?.time ?: Long.MAX_VALUE
        }
    }

    private fun irAReservar() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ReservaFragment()) // Asegúrate que este Fragment existe
            .addToBackStack(null)
            .commit()
    }

    // --- SENSOR ---
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            // Controlar que no se ejecute demasiadas veces por segundo
            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Cálculo de agitación
                val acceleration = sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH

                // He subido un poco la sensibilidad a 8 (5 es muy sensible, a veces salta solo)
                if (acceleration > 2) {
                    if (curTime - lastShakeTime > 1500) { // Esperar 1.5 seg entre sacudidas
                        lastShakeTime = curTime

                        // --- AQUÍ ESTABA EL FALLO: FALTABA EL TOAST ---
                        Toast.makeText(requireContext(), "¡Sensor detectado!", Toast.LENGTH_SHORT).show()

                        irAReservar()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}