package com.example.controllaboratorio.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Adapter.AsignacionAdapter
import com.example.controllaboratorio.Models.Asignacion

import com.example.controllaboratorio.R
import com.google.firebase.auth.FirebaseAuth
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

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragment
        val view = inflater.inflate(R.layout.fragment_docente, container, false)

        // Inicializa el RecyclerView
        recyclerView = view.findViewById(R.id.HorarioList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Obtén el usuario actualmente autenticado
        val user = firebaseAuth.currentUser
        user?.let { usuario ->
            // Obtener el nombre del docente
            val userId = usuario.uid
            firestore.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombreDocente = document.getString("nombre")
                        // Obtener asignaciones filtradas por nombre del docente
                        obtenerAsignaciones(nombreDocente)
                    }
                }
        }

        return view
    }

    private fun obtenerAsignaciones(nombreDocente: String?) {
        // Consulta las asignaciones y filtra solo las que corresponden al docente
        firestore.collection("asignaciones")
            .get()
            .addOnSuccessListener { result ->
                listaAsignaciones = result.filter {
                    val docenteNombre = it.getString("docenteNombre")
                    docenteNombre == nombreDocente
                }.map { it.toObject(Asignacion::class.java) }

                // Configura el Adapter para mostrar los datos en el RecyclerView
                asignacionAdapter = AsignacionAdapter(listaAsignaciones) { asignacion ->
                    // Manejar clic en una asignación
                }
                recyclerView.adapter = asignacionAdapter
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DocenteFragment().apply {
                // Se pueden agregar parámetros si es necesario
            }
    }
}