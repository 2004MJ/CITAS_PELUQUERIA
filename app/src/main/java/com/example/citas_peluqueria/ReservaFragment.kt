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

// Imports de tus clases API (Asegúrate de que el paquete es correcto)
import com.example.citas_peluqueria.api.Cita
import com.example.citas_peluqueria.api.Peluqueria
import com.example.citas_peluqueria.api.Servicio
import com.example.citas_peluqueria.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservaFragment : Fragment() {

    // Variables para guardar lo que seleccione el usuario
    private var fechaSeleccionada = ""
    private var horaSeleccionada = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva, container, false)

        // 1. Configurar el Spinner
        val spinner: Spinner = view.findViewById(R.id.spinner_servicios)
        // El orden importa: Posición 0 = ID 1 en BD, Posición 1 = ID 2...
        val opciones = listOf("Corte de Caballero", "Corte + Barba", "Tinte", "Barbería", "Manicura")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones)
        spinner.adapter = adapter

        // 2. Configurar Botón FECHA
        val btnFecha: Button = view.findViewById(R.id.button_fecha)
        btnFecha.setOnClickListener {
            mostrarCalendario(btnFecha)
        }

        // 3. Configurar Botón HORA
        val btnHora: Button = view.findViewById(R.id.button_hora)
        btnHora.setOnClickListener {
            mostrarReloj(btnHora)
        }

        // 4. Configurar Botón CONFIRMAR
        val btnConfirmar: Button = view.findViewById(R.id.button_confirmar_reserva)
        btnConfirmar.setOnClickListener {
            guardarReservaReal()
        }

        return view
    }

    private fun mostrarCalendario(boton: Button) {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, anio, mes, dia ->
            // IMPORTANTE: Formato YYYY-MM-DD para Spring Boot
            val fechaFormateada = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
            fechaSeleccionada = fechaFormateada
            boton.text = "Fecha: $fechaFormateada"
        }, year, month, day)

        datePicker.show()
    }

    private fun mostrarReloj(boton: Button) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minutos = calendario.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(requireContext(), { _, h, m ->
            // Formato HH:mm
            val horaFormateada = String.format("%02d:%02d", h, m)
            horaSeleccionada = horaFormateada
            boton.text = "Hora: $horaFormateada"
        }, hora, minutos, true)

        timePicker.show()
    }

    // --- FUNCIÓN DE CONEXIÓN REAL ---
    private fun guardarReservaReal() {
        if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
            Toast.makeText(context, "Por favor elige fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        val spinner: Spinner = requireView().findViewById(R.id.spinner_servicios)
        // Calculamos ID: Posición 0 -> ID 1
        val servicioId = (spinner.selectedItemPosition + 1).toLong()

        // Creamos la cita con el usuario fijo "usuario_app"
        // Este mismo usuario es el que luego lee MisReservasFragment
        val nuevaCita = Cita(
            clienteUid = "usuario_app",
            fecha = fechaSeleccionada,
            hora = horaSeleccionada,
            peluqueria = Peluqueria(1), // Gracias al valor por defecto en Peluqueria.kt, esto funciona solo con ID
            servicio = Servicio(servicioId)
        )

        // Enviar a Spring Boot
        RetrofitClient.getApi().crearCita(nuevaCita).enqueue(object : Callback<Cita> {
            override fun onResponse(call: Call<Cita>, response: Response<Cita>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "¡Reserva guardada con éxito!", Toast.LENGTH_LONG).show()

                    // AL TERMINAR: Nos vamos a la pantalla de "Mis Reservas" para verla
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