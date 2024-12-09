package com.example.controllaboratorio.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Adapter.AccesoAdapter
import com.example.controllaboratorio.Models.Acceso
import com.example.controllaboratorio.R
import com.google.firebase.firestore.FirebaseFirestore

class ReporteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var accesoAdapter: AccesoAdapter
    private var listaAccesos: List<Acceso> = mutableListOf()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragment
        val view = inflater.inflate(R.layout.fragment_reporte, container, false)

        // Inicializa el RecyclerView
        recyclerView = view.findViewById(R.id.HistorialLista)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Obtiene el ID del docente desde SharedPreferences
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val docenteId = sharedPreferences.getString("userId", "") ?: ""

        // Obtiene el rol del usuario desde Firestore
        obtenerRol(docenteId)

        return view
    }

    private fun obtenerRol(docenteId: String) {
        // Consulta la base de datos de docentes para obtener el rol de este docenteId
        firestore.collection("usuarios")
            .document(docenteId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rol = document.getString("rol") // Suponiendo que el campo en la base de datos es 'rol'

                    // Si es administrador, obtener todos los accesos, sino solo los del docente
                    if (rol == "administrador") {
                        obtenerTodosLosAccesos()
                    } else {
                        obtenerNumeroTarjeta(docenteId)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Maneja cualquier error al obtener el rol del usuario
            }
    }

    private fun obtenerNumeroTarjeta(docenteId: String) {
        // Consulta la base de datos de docentes para obtener el numeroTarjeta de este docenteId
        firestore.collection("usuarios")
            .document(docenteId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val numeroTarjeta = document.getLong("NumTarjeta") // Suponiendo que el campo en la base de datos es 'numeroTarjeta'

                    if (numeroTarjeta != null) {
                        // Una vez obtenemos el numeroTarjeta, hacemos la consulta a accesos
                        obtenerAccesosPorNumeroTarjeta(numeroTarjeta)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Maneja cualquier error al obtener el docente
            }
    }

    private fun obtenerAccesosPorNumeroTarjeta(numeroTarjeta: Long) {
        // Ahora, consulta la colección de accesos y filtra por numeroTarjeta
        firestore.collection("accesos")
            .whereEqualTo("numeroTarjeta", numeroTarjeta)
            .get()
            .addOnSuccessListener { result ->
                listaAccesos = result.map { it.toObject(Acceso::class.java) }

                // Configura el Adapter para mostrar los datos en el RecyclerView
                accesoAdapter = AccesoAdapter(listaAccesos) { acceso ->
                    // Manejar clic en una tarjeta
                }
                recyclerView.adapter = accesoAdapter
            }
            .addOnFailureListener { exception ->
                // Maneja cualquier error al obtener los accesos
            }
    }

    private fun obtenerTodosLosAccesos() {
        // Consulta todos los accesos si el usuario es administrador
        firestore.collection("accesos")
            .get()
            .addOnSuccessListener { result ->
                listaAccesos = result.map { it.toObject(Acceso::class.java) }

                // Configura el Adapter para mostrar los datos en el RecyclerView
                accesoAdapter = AccesoAdapter(listaAccesos) { acceso ->
                    // Manejar clic en una tarjeta
                }
                recyclerView.adapter = accesoAdapter
            }
            .addOnFailureListener { exception ->
                // Maneja cualquier error al obtener los accesos
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ReporteFragment().apply {
                // Puedes agregar argumentos si es necesario
            }
    }
}
