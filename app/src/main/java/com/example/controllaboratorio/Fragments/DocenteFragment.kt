package com.example.controllaboratorio.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Adapter.AsignacionAdapter
import com.example.controllaboratorio.Models.Asignacion

import com.example.controllaboratorio.R
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [DocenteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DocenteFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var asignacionAdapter: AsignacionAdapter
    private var listaAsignaciones: List<Asignacion> = mutableListOf()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_docente, container, false)

        // Inicializa el RecyclerView
        recyclerView = view.findViewById(R.id.HorarioList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Cargar las asignaciones para el docente logeado
        obtenerAsignaciones()

        return view
    }

    private fun obtenerAsignaciones() {
        // Accede a SharedPreferences para obtener el ID del usuario logeado
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getString("userId", null)

        if (usuarioId.isNullOrEmpty()) {
            Toast.makeText(context, "No se encontró el usuario logeado", Toast.LENGTH_SHORT).show()
            return
        }

        // Busca el nombre del docente en Firestore usando el ID
        firestore.collection("usuarios") // Supongamos que la colección se llama "docentes"
            .document(usuarioId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombreDocente = document.getString("nombre")
                    if (!nombreDocente.isNullOrEmpty()) {
                        // Una vez obtenido el nombre, busca las asignaciones
                        cargarAsignacionesPorDocente(nombreDocente)
                    } else {
                        Toast.makeText(context, "No se encontró el nombre del docente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "El usuario no está registrado como docente", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al obtener datos del docente: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun cargarAsignacionesPorDocente(nombreDocente: String) {
        firestore.collection("asignaciones")
            .whereEqualTo("docente.nombre", nombreDocente) // Filtrar por nombre del docente
            .get()
            .addOnSuccessListener { result ->
                listaAsignaciones = result.map { it.toObject(Asignacion::class.java) }

                // Configurar el adapter con las asignaciones
                asignacionAdapter = AsignacionAdapter(listaAsignaciones) { asignacion ->
                    // Manejar clic en la tarjeta si es necesario
                }
                recyclerView.adapter = asignacionAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar las asignaciones: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        @JvmStatic
        fun newInstance(nombreDocente: String) =
            DocenteFragment().apply {
                arguments = Bundle().apply {
                    putString("nombreDocente", nombreDocente)
                }
            }
    }
}