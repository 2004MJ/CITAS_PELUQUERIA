package com.example.citas_peluqueria

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HorasAdapter(
    private val todasLasHoras: List<String>,   // Ej: ["08:00", "09:00", ...]
    private val horasOcupadas: List<String>,   // Ej: ["10:00", "17:00"]
    private val onHoraClick: (String) -> Unit  // Acción al pulsar
) : RecyclerView.Adapter<HorasAdapter.HoraViewHolder>() {

    private var horaSeleccionada: String = ""

    class HoraViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHora: TextView = view as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoraViewHolder {
        // Usamos un diseño estándar de Android para no crear más archivos XML
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView

        // Ajustes visuales para que parezca un botón/cuadradito
        view.gravity = Gravity.CENTER
        view.layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            120 // Altura del botón
        ).apply {
            setMargins(8, 8, 8, 8) // Separación entre botones
        }

        return HoraViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoraViewHolder, position: Int) {
        val hora = todasLasHoras[position]
        holder.tvHora.text = hora

        // --- LÓGICA DE COLORES ---

        // 1. Limpiamos estilos previos
        holder.tvHora.setBackgroundColor(Color.WHITE)
        holder.tvHora.setTextColor(Color.BLACK)
        holder.itemView.isEnabled = true

        if (horasOcupadas.contains(hora)) {
            // CASO A: HORA OCUPADA (Rojo suave)
            holder.tvHora.setBackgroundColor(Color.parseColor("#FFCDD2"))
            holder.tvHora.setBackgroundColor(Color.parseColor("#FFCDD2"))
            holder.tvHora.setTextColor(Color.GRAY)
            holder.tvHora.text = "$hora\n(Ocupado)"
            holder.itemView.isEnabled = false // No se puede pulsar

        } else if (hora == horaSeleccionada) {
            // CASO B: HORA SELECCIONADA POR TI (Verde)
            holder.tvHora.setBackgroundColor(Color.parseColor("#C8E6C9"))
            holder.tvHora.setTypeface(null, android.graphics.Typeface.BOLD)

        } else {
            // CASO C: HORA LIBRE (Gris clarito de fondo para que parezca botón)
            holder.tvHora.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        // --- CLICK ---
        holder.itemView.setOnClickListener {
            if (!horasOcupadas.contains(hora)) {
                horaSeleccionada = hora
                notifyDataSetChanged() // Refrescamos para actualizar colores
                onHoraClick(hora)
            }
        }
    }

    override fun getItemCount() = todasLasHoras.size
}