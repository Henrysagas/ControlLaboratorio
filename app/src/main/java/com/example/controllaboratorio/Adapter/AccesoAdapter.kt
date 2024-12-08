package com.example.controllaboratorio.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Models.Acceso
import com.example.controllaboratorio.R

class AccesoAdapter (private val listaAccesos: List<Acceso>,
                     private val onItemClick: (Acceso) -> Unit
) : RecyclerView.Adapter<AccesoAdapter.AccesoViewHolder>() {

    // ViewHolder que contiene las vistas para cada elemento
    class AccesoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val asignacionDocenteCurso: TextView = itemView.findViewById(R.id.CursoA)
        val asignacionLaboratorio: TextView = itemView.findViewById(R.id.LAB)
        val fecha: TextView = itemView.findViewById(R.id.FECHA)
        val horaEntrada: TextView = itemView.findViewById(R.id.HORAENTRADA)
        val horaSalida: TextView = itemView.findViewById(R.id.HoraSalida)
        val estado: TextView = itemView.findViewById(R.id.DiaA)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccesoViewHolder {
        // Infla el layout de cada ítem del RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_acceso, parent, false)
        return AccesoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AccesoViewHolder, position: Int) {
        // Vincula los datos con las vistas
        val acceso = listaAccesos[position]

        holder.asignacionDocenteCurso.text =
            (acceso.asignacion?.Docente?.Nombre + " - " + acceso.asignacion?.Curso?.nombreCurso)
                ?: "Sin asignar"
        holder.asignacionLaboratorio.text = acceso.asignacion?.Laboratorio?.nombreLab ?: "Sin asignar"
        holder.fecha.text = acceso.fecha?.toString() ?: "Sin fecha"
        holder.horaEntrada.text = acceso.horaEntrada?.toString() ?: " "
        holder.horaSalida.text = acceso.horaSalida?.toString() ?: " "
        holder.estado.text = acceso.Estado ?: "Sin estado"

        // Maneja clics en el elemento
        holder.itemView.setOnClickListener { onItemClick(acceso) }
    }

    override fun getItemCount(): Int {
        // Devuelve el tamaño de la lista
        return listaAccesos.size
    }
}
