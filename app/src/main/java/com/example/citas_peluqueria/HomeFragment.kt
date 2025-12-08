package com.example.citas_peluqueria // Asegúrate de que esto sea correcto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.citas_peluqueria.api.Peluqueria
import com.example.citas_peluqueria.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el XML nuevo que te acabo de pasar
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 1. Configuramos el RecyclerView (la lista)
        recycler = view.findViewById(R.id.recyclerPeluquerias)
        recycler.layoutManager = LinearLayoutManager(context)

        // 2. Configuramos el Botón "Reserva Rápida"
        val btnReserva = view.findViewById<Button>(R.id.btn_reservar_ahora)
        btnReserva.setOnClickListener {
            // Navegamos al fragmento de reservas
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ReservaFragment())
                .addToBackStack(null)
                .commit()
        }

        // 3. Cargamos los datos de internet
        cargarDatos()

        return view
    }

    private fun cargarDatos() {
        val api = RetrofitClient.getApi()
        api.obtenerPeluquerias().enqueue(object : Callback<List<Peluqueria>> {
            override fun onResponse(
                call: Call<List<Peluqueria>>,
                response: Response<List<Peluqueria>>
            ) {
                if (response.isSuccessful) {
                    val listaPeluquerias = response.body()
                    if (listaPeluquerias != null) {
                        // Asignamos el adaptador (asegúrate de haber actualizado el adaptador también)
                        recycler.adapter = UbicacionesAdapter(listaPeluquerias)
                    }
                } else {
                    Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Peluqueria>>, t: Throwable) {
                Toast.makeText(context, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}