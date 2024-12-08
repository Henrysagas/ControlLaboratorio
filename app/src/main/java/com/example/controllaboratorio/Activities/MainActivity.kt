package com.example.controllaboratorio.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.controllaboratorio.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val userRole = sharedPreferences.getString("rol", null)

        if (userId != null && userRole != null) {
            val intent = when(userRole) {
                "administrador" -> Intent(this, HomeActivity::class.java)
                "docente" -> Intent(this, DocenteActivity::class.java)
                else -> null
            }
            intent?.let{
                startActivity(it)
                finish()
                return
            }
        }

        setContentView(R.layout.activity_main)

        //Configura los elementos del layout
        val emailEditText = findViewById<TextInputEditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password_edit_text)
        val loginButton = findViewById<MaterialButton>(R.id.LoginButtom)

        val firestore = FirebaseFirestore.getInstance()
        // Configura el botón de inicio de sesión

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firestore.collection("usuarios")
                .whereEqualTo("correo", email)
                .whereEqualTo("contrasena", password)
                .get()
                .addOnSuccessListener{
                    documents ->
                    if ( documents.isEmpty) {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    } else {
                        val user = documents.first()
                        val userId = user.id
                        val rol = user.getString("rol") ?: ""

                        sharedPreferences.edit()
                            .putString("userId", userId)
                            .putString("rol", rol)
                            .apply()

                        when (rol){
                            "administrador" -> {
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.putExtra("userId", userId)
                                startActivity(intent)
                                finish()
                            }
                            "docente" -> {
                                val intent = Intent(this, DocenteActivity::class.java)
                                intent.putExtra("userId", userId)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .addOnFailureListener{ exception ->
                    Toast.makeText(this, "Error al iniciar sesion: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
