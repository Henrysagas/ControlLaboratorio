package com.example.controllaboratorio.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Models.Asignacion
import com.example.controllaboratorio.R

class AsignacionAdapter (private val listaAsignaciones: List<Asignacion>,
                         private val onItemClick: (Asignacion) -> Unit
) : RecyclerView.Adapter<AsignacionAdapter.AsignacionViewHolder>() {

    // ViewHolder que contiene las vistas para cada elemento
    class AsignacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val laboratorio: TextView = itemView.findViewById(R.id.LabA)
        val curso: TextView = itemView.findViewById(R.id.CursoA)
        val dia: TextView = itemView.findViewById(R.id.DiaA)
        val horaES: TextView = itemView.findViewById(R.id.HoraES)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsignacionViewHolder {
        // Infla el layout de cada item del RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_asignacion, parent, false)
        return AsignacionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AsignacionViewHolder, position: Int) {
        // Vincula los datos con las vistas
        val asignacion = listaAsignaciones[position]
        holder.laboratorio.text = asignacion.Laboratorio?.nombreLab
        holder.curso.text = asignacion.Curso?.nombreCurso + " - Grupo " + asignacion.Curso?.grupo
        holder.dia.text = asignacion.Dia
        holder.horaES.text = asignacion.horaEntrada.toString() + " - " +  asignacion.horaSalida.toString()

        // Maneja clics en el elemento
        holder.itemView.setOnClickListener { onItemClick(asignacion) }
    }

    override fun getItemCount(): Int {
        // Devuelve el tamaño de la lista
        return listaAsignaciones.size
    }
}