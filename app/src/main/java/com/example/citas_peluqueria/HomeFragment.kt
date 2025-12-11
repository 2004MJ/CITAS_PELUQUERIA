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
            sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        } catch (e: Exception) { }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // BÚSQUEDA DE VISTAS SEGURA (Con Casting clásico)
        val btnPedirCita = view.findViewById(R.id.btnPedirCita) as Button
        btnPedirCita.setOnClickListener { irAReservar() }

        val cardProximaCita = view.findViewById(R.id.cardProximaCita) as View
        val tvDetalle = view.findViewById(R.id.tvDetalleCitaHome) as TextView

        cardProximaCita.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MisReservasFragment())
                .addToBackStack(null)
                .commit()
        }

        cargarProximaCita(tvDetalle)
    }

    private fun cargarProximaCita(textView: TextView) {
        // Usa tu llamada habitual aquí
        // RetrofitClient.getApi()...
    }

    private fun irAReservar() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ReservaFragment())
            .addToBackStack(null)
            .commit()
    }

    // --- SENSOR ---
    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val acceleration = sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH

                if (acceleration > 5) {
                    if (curTime - lastShakeTime > 1000) {
                        lastShakeTime = curTime
                        irAReservar()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}