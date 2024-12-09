package com.example.controllaboratorio.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.controllaboratorio.Activities.MainActivity
import com.example.controllaboratorio.Models.Usuario

import com.example.controllaboratorio.R
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {

    private lateinit var nombreTextView: TextView
    private lateinit var correoTextView: TextView
    private lateinit var rolTextView: TextView
    private lateinit var tarjetaTextView: TextView

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Inicializa las vistas
        nombreTextView = view.findViewById(R.id.Nombre)
        correoTextView = view.findViewById(R.id.Correo)
        rolTextView = view.findViewById(R.id.Rol)
        tarjetaTextView = view.findViewById(R.id.TarjetaID)
        val cerrarSesionButton = view.findViewById<Button>(R.id.button3)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "Error: Sesión no encontrada", Toast.LENGTH_SHORT).show()
            return view
        }

        // Obtener los datos del usuario desde Firestore
        firestore.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Extraer los datos del documento
                    val nombre = document.getString("nombre")
                    val correo = document.getString("correo")
                    val rol = document.getString("rol")
                    val numTarjeta = document.getLong("NumTarjeta")
                    val usuario = Usuario(nombre, correo, rol, numTarjeta)
                    // Mostrar los datos en las vistas
                    nombreTextView.text = "Nombre: ${usuario.Nombre ?: "No disponible"}"
                    correoTextView.text = "Correo: ${usuario.Correo ?: "No disponible"}"
                    rolTextView.text = "Rol: ${usuario.Rol ?: "No disponible"}"
                    tarjetaTextView.text = "Número de Tarjeta: ${usuario.NumTarjeta ?: "No disponible"}"
                } else {
                    nombreTextView.text = "Usuario no encontrado"
                }
            }
            .addOnFailureListener { exception ->
                // Manejar error al obtener los datos
                Toast.makeText(requireContext(), "Error al obtener datos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        cerrarSesionButton.setOnClickListener{
            cerrarSesion()
        }

        return view
    }

    private fun cerrarSesion() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", 0)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
    }

}