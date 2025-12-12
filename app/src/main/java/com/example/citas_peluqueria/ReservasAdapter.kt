package com.example.citas_peluqueria

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citas_peluqueria.api.Cita
import java.text.SimpleDateFormat
import java.util.*

class ReservasAdapter(
    private val listaCitas: MutableList<Cita>,
    private val onCancelarClick: (Cita) -> Unit
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

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

        // 1. Asignamos los textos básicos
        holder.tvServicio.text = cita.servicio.nombre
        holder.tvDia.text = cita.fecha
        holder.tvHora.text = cita.hora
        holder.tvLugar.text = cita.peluqueria.nombre

        // 2. LÓGICA DE FECHAS (NUEVO)
        // --------------------------------------------------------
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val ahora = Date()

        // Unimos fecha y hora para comparar: "2025-05-10 14:30"
        val fechaString = "${cita.fecha} ${cita.hora}"
        var yaPaso = false

        try {
            val fechaCita = sdf.parse(fechaString)
            if (fechaCita != null && fechaCita.before(ahora)) {
                yaPaso = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. CAMBIO VISUAL SEGÚN EL ESTADO
        if (yaPaso) {
            // --- CASO: CITA PASADA ---
            // Oscurecemos la fila para que parezca "historial"
            holder.itemView.alpha = 0.5f

            holder.tvServicio.setTextColor(Color.GRAY)
            holder.btnCancelar.text = "BORRAR"
            // Opcional: Cambiar color del botón a gris oscuro si quisieras
            // holder.btnCancelar.setBackgroundColor(Color.DKGRAY)

        } else {
            // --- CASO: CITA FUTURA (PENDIENTE) ---
            // Restauramos la opacidad y colores (Importante por el reciclaje de vistas)
            holder.itemView.alpha = 1.0f

            // Asumiendo que tu texto normal es blanco o el defecto del tema
            holder.tvServicio.setTextColor(Color.WHITE)
            holder.btnCancelar.text = "CANCELAR"
        }
        // --------------------------------------------------------

        // Configuramos el botón (sirve tanto para cancelar como para borrar historial)
        holder.btnCancelar.setOnClickListener {
            onCancelarClick(cita)
        }
    }

    override fun getItemCount() = listaCitas.size
}