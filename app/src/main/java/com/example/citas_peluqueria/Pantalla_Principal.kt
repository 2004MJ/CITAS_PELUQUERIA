package com.example.citas_peluqueria

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback // ¡¡IMPORTANTE: Añadir este import!!
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.citas_peluqueria.databinding.ActivityPantallaPrincipalBinding
import com.google.android.material.navigation.NavigationView

class Pantalla_Principal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityPantallaPrincipalBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- INICIO: CÓDIGO PARA EL MENÚ LATERAL (DrawerLayout) ---
        setSupportActionBar(binding.topAppBar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.topAppBar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationView.setNavigationItemSelectedListener(this)
        // --- FIN: CÓDIGO PARA EL MENÚ LATERAL ---

        // --- INICIO: CÓDIGO PARA LA NAVEGACIÓN INFERIOR (BottomNav) ---
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.navigationView.setCheckedItem(R.id.nav_inicio)
            binding.topAppBar.title = "Inicio"
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    binding.topAppBar.title = "Inicio"
                }
                R.id.nav_reservas -> {
                    replaceFragment(ReservaFragment())
                    binding.topAppBar.title = "Mis Reservas"
                }
                R.id.nav_mapa -> {
                    replaceFragment(MapaFragment())
                    binding.topAppBar.title = "Mapa"
                }
            }
            true
        }
        // --- FIN: CÓDIGO PARA LA NAVEGACIÓN INFERIOR ---

        // --- INICIO: NUEVA GESTIÓN DEL BOTÓN "ATRÁS" ---
        // Este bloque reemplaza el antiguo método onBackPressed()
        onBackPressedDispatcher.addCallback(this) {
            // Comprueba si el menú lateral está abierto
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                // Si lo está, lo cierra
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                // Si no, ejecuta el comportamiento por defecto (salir de la app, etc.)
                // Para ello, debe estar habilitado
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        // --- FIN: NUEVA GESTIÓN DEL BOTÓN "ATRÁS" ---
    }

    // Gestiona los clics en los elementos del menú lateral.
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_inicio -> {
                replaceFragment(HomeFragment())
                binding.topAppBar.title = "Inicio"
            }
            R.id.nav_reservas -> {
                replaceFragment(ReservaFragment())
                binding.topAppBar.title = "Mis Reservas"
            }
            R.id.nav_contacto -> {
                Toast.makeText(this, "Abriendo contacto...", Toast.LENGTH_SHORT).show()
                binding.topAppBar.title = "Contáctanos"
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // EL ANTIGUO MÉTODO onBackpressed() SE HA ELIMINADO

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
