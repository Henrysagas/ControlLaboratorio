package com.example.controllaboratorio.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Models.Acceso
import com.example.controllaboratorio.R

class AccesoAdapter(
    private val listaAccesos: List<Acceso>,
    private val onItemClick: (Acceso) -> Unit
) : RecyclerView.Adapter<AccesoAdapter.AccesoViewHolder>() {

    // ViewHolder que contiene las vistas para cada elemento
    class AccesoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val docenteCursoTextView: TextView = itemView.findViewById(R.id.DocenteCurso)
        val laboratorioTextView: TextView = itemView.findViewById(R.id.LAB)
        val fechaTextView: TextView = itemView.findViewById(R.id.FECHA)
        val horaEntradaTextView: TextView = itemView.findViewById(R.id.HORAENTRADA)
        val horaSalidaTextView: TextView = itemView.findViewById(R.id.HoraSalida)
        val estadoTextView: TextView = itemView.findViewById(R.id.Estado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccesoViewHolder {
        // Infla el layout de cada ítem del RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_acceso, parent, false)
        return AccesoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AccesoViewHolder, position: Int) {
        // Obtén el objeto Acceso correspondiente
        val acceso = listaAccesos[position]

        // Asigna los valores de las propiedades del modelo `Acceso` a los TextViews
        holder.docenteCursoTextView.text =
            "${acceso.nombreDocente} - ${acceso.curso}"  // Docente y Curso
        holder.laboratorioTextView.text = acceso.laboratorio?.nombreLab ?: "Sin asignar"  // Nombre del laboratorio
        holder.fechaTextView.text = acceso.fecha ?: "Sin fecha"  // Fecha

        // Hora de entrada: asignada y real
        holder.horaEntradaTextView.text =
            "Asignada: ${acceso.horaEntrada ?: "N/A"}\nReal: ${acceso.horaGlobalLima ?: "N/A"}"

        // Hora de salida: asignada y real
        holder.horaSalidaTextView.text =
            "Asignada: ${acceso.horaSalida ?: "N/A"}\nReal: ${acceso.horaSalidaReal ?: "N/A"}"

        // Estado: Abierto o Cerrado
        holder.estadoTextView.text = if (acceso.cerrado == true) {
            "Cerrado"
        } else {
            "Abierto"
        }

        // Maneja clic en el item
        holder.itemView.setOnClickListener { onItemClick(acceso) }
    }


    override fun getItemCount(): Int {
        // Devuelve el tamaño de la lista de accesos
        return listaAccesos.size
    }
}
