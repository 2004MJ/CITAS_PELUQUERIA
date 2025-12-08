package com.example.citas_peluqueria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citas_peluqueria.api.Reserva

class MisReservasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_reservas, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerMisReservas)
        recycler.layoutManager = LinearLayoutManager(context)

        // DATOS DE PRUEBA (Luego conectaremos esto con tu Base de Datos real)
        val listaPrueba = mutableListOf(
            Reserva(1, "Corte Degradado", "10 Dic", "10:00", "Barbería Style", "Calle Mayor 4"),
            Reserva(2, "Tinte y Mechas", "12 Dic", "17:30", "Salón Glamour", "Av. Libertad 20"),
            Reserva(3, "Arreglo Barba", "15 Dic", "11:00", "Barber Shop", "Plaza Norte")
        )

        // Configurar el adaptador
        val adapter = ReservasAdapter(listaPrueba) { reservaACancelar ->
            // Aquí irá la lógica para borrar de la base de datos
            Toast.makeText(context, "Cancelando cita: ${reservaACancelar.nombreServicio}", Toast.LENGTH_SHORT).show()

            // Simulación visual de borrado
            listaPrueba.remove(reservaACancelar)
            recycler.adapter?.notifyDataSetChanged()
        }

        recycler.adapter = adapter

        return view
    }
}