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

class ReservaFragment : Fragment() {

    // Variables para guardar lo que seleccione el usuario
    private var fechaSeleccionada = ""
    private var horaSeleccionada = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Asegúrate de que tu archivo XML se llame 'fragment_reserva'
        // Si le pusiste otro nombre, cámbialo aquí donde dice R.layout.xxxx
        val view = inflater.inflate(R.layout.fragment_reserva, container, false)

        // 1. Configurar el Spinner (Lista desplegable de servicios)
        // Buscamos el spinner por el ID que pusiste en tu XML
        val spinner: Spinner = view.findViewById(R.id.spinner_servicios)

        // Creamos una lista de opciones
        val opciones = listOf("Corte de Caballero", "Corte de Dama", "Tinte", "Barbería", "Manicura")

        // Conectamos la lista al spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones)
        spinner.adapter = adapter

        // 2. Configurar el Botón de FECHA
        val btnFecha: Button = view.findViewById(R.id.button_fecha)
        btnFecha.setOnClickListener {
            mostrarCalendario(btnFecha)
        }

        // 3. Configurar el Botón de HORA
        val btnHora: Button = view.findViewById(R.id.button_hora)
        btnHora.setOnClickListener {
            mostrarReloj(btnHora)
        }

        // 4. Configurar el Botón CONFIRMAR
        val btnConfirmar: Button = view.findViewById(R.id.button_confirmar_reserva)
        btnConfirmar.setOnClickListener {
            val servicio = spinner.selectedItem.toString()
            guardarReserva(servicio)
        }

        return view
    }

    // Función para mostrar el calendario
    private fun mostrarCalendario(boton: Button) {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, anio, mes, dia ->
            // El mes empieza en 0, así que le sumamos 1
            val fecha = "$dia/${mes + 1}/$anio"
            fechaSeleccionada = fecha
            boton.text = "Fecha: $fecha" // Cambiamos el texto del botón
        }, year, month, day)

        datePicker.show()
    }

    // Función para mostrar el reloj
    private fun mostrarReloj(boton: Button) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minutos = calendario.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(requireContext(), { _, h, m ->
            // Formateamos para que salga 09:05 en vez de 9:5
            val horaFormateada = String.format("%02d:%02d", h, m)
            horaSeleccionada = horaFormateada
            boton.text = "Hora: $horaFormateada" // Cambiamos el texto del botón
        }, hora, minutos, true)

        timePicker.show()
    }

    // Función para simular el guardado
    private fun guardarReserva(servicio: String) {
        if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
            Toast.makeText(context, "Por favor elige fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulación de éxito
        Toast.makeText(context, "¡Reserva Confirmada! $servicio el $fechaSeleccionada", Toast.LENGTH_LONG).show()

        // Volvemos a la pantalla de "Mis Reservas"
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MisReservasFragment())
            .addToBackStack(null)
            .commit()
    }
}