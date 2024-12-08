package com.example.controllaboratorio.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.example.controllaboratorio.R
import com.google.firebase.auth.FirebaseAuth
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

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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

        // Obtén el usuario actualmente autenticado
        val user = firebaseAuth.currentUser

        // Asegúrate de que el usuario esté autenticado
        user?.let { usuario ->
            val userId = usuario.uid
            // Obtener los datos del usuario desde Firestore
            firestore.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Extraer los datos del documento
                        val nombre = document.getString("nombre")
                        val correo = document.getString("correo")
                        val rol = document.getString("rol")
                        val numTarjeta = document.getString("numTarjeta")

                        // Mostrar los datos en las vistas
                        nombreTextView.text = "Nombre: ${nombre ?: "No disponible"}"
                        correoTextView.text = "Correo: ${correo ?: "No disponible"}"
                        rolTextView.text = "Rol: ${rol ?: "No disponible"}"
                        tarjetaTextView.text = "Número de Tarjeta: ${numTarjeta ?: "No disponible"}"
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar error al obtener los datos
                }
        }

        return view
    }
}