package com.example.controllaboratorio.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.controllaboratorio.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Configura los elementos del layout
        val emailEditText = findViewById<TextInputEditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password_edit_text)
        val loginButton = findViewById<MaterialButton>(R.id.LoginButtom)

        // Configura el botón de inicio de sesión

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }

        }
    }

}
