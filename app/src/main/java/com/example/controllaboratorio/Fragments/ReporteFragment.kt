package com.example.controllaboratorio.Fragments

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [ReporteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        // Se obtiene el rol del usuario desde los argumentos
        val rol = arguments?.getString("rol") ?: "Docente"
        val docenteId = arguments?.getString("docenteId") ?: ""

        // Obtener accesos según el rol
        obtenerAccesos(rol, docenteId)

        return view
    }

    private fun obtenerAccesos(rol: String, docenteId: String) {
        // Consulta los accesos dependiendo del rol
        firestore.collection("accesos")
            .get()
            .addOnSuccessListener { result ->
                listaAccesos = if (rol == "Administrador") {
                    // Si es Administrador, se muestran todos los accesos
                    result.map { it.toObject(Acceso::class.java) }
                } else {
                    // Si es Docente, filtra solo los accesos del docente
                    result.filter {
                        val id = it.getString("docenteId")
                        id == docenteId
                    }.map { it.toObject(Acceso::class.java) }
                }

                // Configura el Adapter para mostrar los datos en el RecyclerView
                accesoAdapter = AccesoAdapter(listaAccesos) { acceso ->
                    // Manejar clic en una tarjeta
                }
                recyclerView.adapter = accesoAdapter
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(rol: String, docenteId: String) =
            ReporteFragment().apply {
                arguments = Bundle().apply {
                    putString("rol", rol)
                    putString("docenteId", docenteId)
                }
            }
    }
}