package com.example.citas_peluqueria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citas_peluqueria.api.Cita

class ReservasAdapter(
    private val listaCitas: MutableList<Cita>,
    private val onCancelarClick: (Cita) -> Unit
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    // ViewHolder clásico: Buscamos las vistas por su ID manualmente
    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvServicio: TextView = view.findViewById(R.id.tvServicio)
        val tvDia: TextView = view.findViewById(R.id.tvDia)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvLugar: TextView = view.findViewById(R.id.tvLugar)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val cita = listaCitas[position]

        // Asignamos los textos manualmente
        holder.tvServicio.text = cita.servicio.nombre
        holder.tvDia.text = cita.fecha
        holder.tvHora.text = cita.hora
        holder.tvLugar.text = cita.peluqueria.nombre

        // Configuramos el botón
        holder.btnCancelar.setOnClickListener {
            onCancelarClick(cita)
        }
    }

    override fun getItemCount() = listaCitas.size
}