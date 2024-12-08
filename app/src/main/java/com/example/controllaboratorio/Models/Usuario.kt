package com.example.controllaboratorio.Models

import java.time.LocalTime

class Usuario {
    var Nombre: String? = null
    var Correo: String? = null
    var Rol: String? = null
    var NumTarjeta: Long? = null

    constructor() {
        // Dejar vacío o inicializar con valores predeterminados si es necesario
    }

    constructor(Nombre: String?, Correo: String?, Rol: String?, NumTarjeta: Long?) {
        this.Nombre = Nombre
        this.Correo = Correo
        this.Rol = Rol
        this.NumTarjeta = NumTarjeta
    }
}