package com.example.citas_peluqueria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth // Importar Firebase Auth

class Login : AppCompatActivity() {

    // 1. Declarar la variable de autenticación
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Ajuste de márgenes (código original)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 3. Referencias a los elementos visuales (Asegúrate de tener estos IDs en tu XML)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginButton)

        // 4. Configurar el botón de Login
        loginBtn.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login correcto
                    Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                    // Aquí irías a la siguiente pantalla (Home/MainActivity)
                    val siguientePantalla1 = Intent(this, Pantalla_Principal::class.java)
                    startActivity(siguientePantalla1)
                } else {
                    // Login fallido
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}