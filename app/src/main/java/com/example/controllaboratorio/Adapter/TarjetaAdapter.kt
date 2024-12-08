package com.example.controllaboratorio.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Models.Usuario
import com.example.controllaboratorio.R

class TarjetaAdapter (private val listaUsuarios: List<Usuario>) : RecyclerView.Adapter<TarjetaAdapter.UsuarioViewHolder>() {

    // ViewHolder que contiene las vistas para cada elemento
    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.DocenteNombre)
        val numTarjeta: TextView = itemView.findViewById(R.id.TarjetaNumero)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        // Infla el layout de cada item del RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_tarjeta, parent, false)
        return UsuarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        // Vincula los datos con las vistas
        val usuario = listaUsuarios[position]
        holder.nombre.text = usuario.Nombre
        holder.numTarjeta.text = usuario.NumTarjeta?.toString() ?: "Sin número"
    }

    override fun getItemCount(): Int {
        // Devuelve el tamaño de la lista
        return listaUsuarios.size
    }
}