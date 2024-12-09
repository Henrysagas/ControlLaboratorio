package com.example.controllaboratorio.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Adapter.TarjetaAdapter
import com.example.controllaboratorio.Models.Usuario

import com.example.controllaboratorio.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * A simple [Fragment] subclass.
 * Use the [LabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LabFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tarjetaAdapter: TarjetaAdapter
    private val listaUsuarios: MutableList<Usuario> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lab, container, false)

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.TarjetasList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Cargar los usuarios desde Firestore
        cargarUsuariosDesdeFirestore()

        return view
    }

    private fun cargarUsuariosDesdeFirestore() {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                listaUsuarios.clear()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val correo = document.getString("correo")
                    val rol = document.getString("rol")
                    val numTarjeta = document.getLong("NumTarjeta")

                    // Solo añadir usuarios con NumTarjeta no nulo y no vacío
                    if (numTarjeta != null) {
                        listaUsuarios.add(Usuario(nombre, correo, rol, numTarjeta))
                    }
                }

                // Actualizar el RecyclerView con los usuarios filtrados
                tarjetaAdapter = TarjetaAdapter(listaUsuarios)
                recyclerView.adapter = tarjetaAdapter
            }
            .addOnFailureListener { exception ->
                // Manejar errores
                println("Error al obtener documentos: $exception")
            }
    }
}