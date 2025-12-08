package com.example.citas_peluqueria // Ajusta el paquete si lo tienes en 'adapter'

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.citas_peluqueria.api.Peluqueria

class UbicacionesAdapter(private val listaPeluquerias: List<Peluqueria>) :
    RecyclerView.Adapter<UbicacionesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Asegúrate de que estos IDs existan en tu item_ubicacion.xml
        val txtNombre: TextView = view.findViewById(R.id.txtNombrePeluqueria)
        val txtDireccion: TextView = view.findViewById(R.id.txtDireccionPeluqueria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ubicacion, parent, false) // Tu diseño XML
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaPeluquerias[position]
        holder.txtNombre.text = item.nombre
        holder.txtDireccion.text = item.direccion
    }

    override fun getItemCount(): Int = listaPeluquerias.size
}