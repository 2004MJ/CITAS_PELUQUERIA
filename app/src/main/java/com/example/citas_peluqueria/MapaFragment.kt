package com.example.citas_peluqueria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.citas_peluqueria.api.Peluqueria
import com.example.citas_peluqueria.api.RetrofitClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Implementamos OnMapReadyCallback para saber cuándo carga el mapa
class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el XML (asegúrate de que fragment_mapa.xml tenga el <fragment> de Google Maps)
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)

        // Buscamos el fragmento del mapa dentro del diseño y lo iniciamos
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    // Este método se ejecuta automáticamente cuando el mapa está listo para usarse
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configuración inicial del mapa (Zoom, tipo de mapa, etc.)
        mMap.uiSettings.isZoomControlsEnabled = true

        // Cargar las peluquerías del servidor para poner las chinchetas
        cargarPeluqueriasEnMapa()

        // Centrar cámara en Murcia (Coordenadas por defecto para que no salga el océano)
        val murcia = LatLng(37.9838, -1.1280)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(murcia, 13f))
    }

    private fun cargarPeluqueriasEnMapa() {
        // Llamada a Spring Boot
        RetrofitClient.getApi().obtenerPeluquerias().enqueue(object : Callback<List<Peluqueria>> {
            override fun onResponse(call: Call<List<Peluqueria>>, response: Response<List<Peluqueria>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()

                    // Recorremos la lista que vino de la base de datos
                    for (p in lista) {
                        // Verificamos que tenga coordenadas válidas (distintas de 0.0)
                        if (p.latitud != 0.0 && p.longitud != 0.0) {
                            val posicion = LatLng(p.latitud, p.longitud)

                            // Añadimos el marcador (Chincheta)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(posicion)
                                    .title(p.nombre) // Nombre de la peluquería al tocar
                                    .snippet(p.direccion) // Dirección al tocar
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Peluqueria>>, t: Throwable) {
                if (context != null) {
                    Toast.makeText(context, "Error al cargar mapa: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}