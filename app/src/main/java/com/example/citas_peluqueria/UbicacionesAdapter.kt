package com.example.citas_peluqueria // Usa tu paquete correcto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// El adaptador recibe una lista de pares (Nombre, Ciudad)
class UbicacionesAdapter(private val ubicaciones: List<Pair<String, String>>) :
    RecyclerView.Adapter<UbicacionesAdapter.UbicacionViewHolder>() {

    // 'ViewHolder' contiene las referencias a las vistas (TextViews) de item_ubicacion.xml
    class UbicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.textView_nombre)
        val ciudadTextView: TextView = itemView.findViewById(R.id.textView_ciudad)
    }

    // Crea una nueva vista (ViewHolder) para un elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UbicacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ubicacion, parent, false)
        return UbicacionViewHolder(view)
    }

    // Conecta los datos de una posición específica con su ViewHolder
    override fun onBindViewHolder(holder: UbicacionViewHolder, position: Int) {
        val ubicacion = ubicaciones[position]
        holder.nombreTextView.text = ubicacion.first
        holder.ciudadTextView.text = ubicacion.second
    }

    // Devuelve el número total de elementos en la lista
    override fun getItemCount() = ubicaciones.size
}
