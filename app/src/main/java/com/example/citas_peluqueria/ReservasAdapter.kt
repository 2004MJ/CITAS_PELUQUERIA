package com.example.citas_peluqueria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citas_peluqueria.api.Reserva

class ReservasAdapter(
    private val listaReservas: MutableList<Reserva>,
    private val onCancelarClick: (Reserva) -> Unit // Acción al pulsar cancelar
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val servicio: TextView = view.findViewById(R.id.tvServicio)
        val fecha: TextView = view.findViewById(R.id.tvDia) // Usamos este para el día
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
        val reserva = listaReservas[position]

        holder.servicio.text = reserva.nombreServicio
        holder.fecha.text = reserva.fecha
        holder.hora.text = reserva.hora
        holder.lugar.text = "${reserva.nombrePeluqueria} - ${reserva.direccion}"

        // Configurar botón cancelar
        holder.btnCancelar.setOnClickListener {
            onCancelarClick(reserva)
        }
    }

    override fun getItemCount() = listaReservas.size
}