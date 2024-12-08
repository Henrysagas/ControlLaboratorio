package com.example.controllaboratorio.Models

import java.time.LocalTime

class Usuario {
    var Nombre: String? = null
    var Correo: String? = null
    var Rol: String? = null
    var NumTarjeta: String? = null

    constructor(Nombre: String?, Correo: String?, Rol: String?, NumTarjeta: String?) {
        this.Nombre = Nombre
        this.Correo = Correo
        this.Rol = Rol
        this.NumTarjeta = NumTarjeta
    }
}