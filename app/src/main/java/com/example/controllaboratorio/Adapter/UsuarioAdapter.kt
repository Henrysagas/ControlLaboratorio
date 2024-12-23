package com.example.controllaboratorio.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controllaboratorio.Models.Usuario
import com.example.controllaboratorio.R

class UsuarioAdapter (
    private val listaUsuarios: List<Usuario>,
    private val onItemClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    // ViewHolder que contiene las vistas para cada elemento
    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.Nombre)
        val correo: TextView = itemView.findViewById(R.id.Correo)
        val rol: TextView = itemView.findViewById(R.id.Rol)
        val numTarjeta: TextView = itemView.findViewById(R.id.TarjetaID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        // Infla el layout de cada ítem del RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_perfil, parent, false)
        return UsuarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        // Vincula los datos con las vistas
        val usuario = listaUsuarios[position]

        // Mostrar cada campo con su etiqueta solo si existe
        holder.nombre.text = usuario.Nombre?.let { "Nombre: $it" } ?: ""
        holder.correo.text = usuario.Correo?.let { "Correo: $it" } ?: ""
        holder.rol.text = usuario.Rol?.let { "Rol: $it" } ?: ""
        holder.numTarjeta.text = usuario.NumTarjeta?.toString()?.takeIf { it.isNotBlank() }?.let { "Número de Tarjeta: $it" } ?: ""
        // Maneja clics en el elemento
        holder.itemView.setOnClickListener { onItemClick(usuario) }
    }

    override fun getItemCount(): Int {
        // Devuelve el tamaño de la lista
        return listaUsuarios.size
    }
}