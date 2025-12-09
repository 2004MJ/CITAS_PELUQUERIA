package com.example.citas_peluqueria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citas_peluqueria.api.Cita // Usamos la clase que ya tienes creada

class ReservasAdapter(
    private val listaCitas: MutableList<Cita>, // Cambiamos 'Reserva' por 'Cita'
    private val onCancelarClick: (Cita) -> Unit // Función para cuando pulses cancelar
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Asegúrate de que estos IDs coinciden con tu item_reserva.xml
        val servicio: TextView = view.findViewById(R.id.tvServicio)
        val fecha: TextView = view.findViewById(R.id.tvDia)
        val hora: TextView = view.findViewById(R.id.tvHora)
        val lugar: TextView = view.findViewById(R.id.tvLugar)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val cita = listaCitas[position]

        // --- AQUÍ CONECTAMOS LOS DATOS REALES DE TU BACKEND ---

        // Accedemos al objeto anidado 'servicio' para sacar el nombre
        holder.servicio.text = cita.servicio.nombre

        holder.fecha.text = cita.fecha
        holder.hora.text = cita.hora

        // Accedemos al objeto anidado 'peluqueria' para sacar nombre y dirección
        holder.lugar.text = "${cita.peluqueria.nombre} - ${cita.peluqueria.direccion}"

        // Configurar botón cancelar
        holder.btnCancelar.setOnClickListener {
            onCancelarClick(cita)
        }
    }

    override fun getItemCount() = listaCitas.size
}