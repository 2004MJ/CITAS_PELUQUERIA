package com.example.citas_peluqueria // Usa tu paquete correcto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MapaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout que ahora contiene el RecyclerView
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Encuentra el RecyclerView en la vista
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_ubicaciones)

        // 2. Define la lista de ubicaciones
        val peluquerias = listOf(
            Pair("Peluquería Style", "Madrid"),
            Pair("Cortes Modernos", "Barcelona"),
            Pair("Look & Feel", "Sevilla"),
            Pair("Salón Glamour", "Valencia"),
            Pair("Tijeras de Oro", "Bilbao"),
            Pair("Ondas Perfectas", "Zaragoza"),
            Pair("Tu Pelo", "Málaga")
        )

        // 3. Configura el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context) // Organiza los elementos en una lista vertical
        recyclerView.adapter = UbicacionesAdapter(peluquerias)     // Asigna el adaptador con los datos
    }
}
