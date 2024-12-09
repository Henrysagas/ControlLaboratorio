package com.example.controllaboratorio.Models

import java.time.LocalDate
import java.time.LocalTime

class Acceso {

    var curso: String? = null
    var fecha: String? = null
    var grupo: String? = null
    var horaEntrada: String? = null
    var horaGlobalLima: String? = null
    var horaIngresada: String? = null
    var horaSalidaReal: String? = null
    var laboratorio: Laboratorio? = null
    var nombreDocente: String? = null
    var numeroTarjeta: Long? = null
    var cerrado: String? = null

    constructor() {
        // Dejar vacío o inicializar con valores predeterminados si es necesario
    }

    constructor(
        curso: String?,
        fecha: String?,
        grupo: String?,
        horaEntrada: String?,
        horaGlobalLima: String?,
        horaIngresada: String?,
        horaSalidaReal: String?,
        laboratorio: Laboratorio?,
        nombreDocente: String?,
        numeroTarjeta: Long?,
        cerrado: String?
    ) {
        this.curso = curso
        this.fecha = fecha
        this.grupo = grupo
        this.horaEntrada = horaEntrada
        this.horaGlobalLima = horaGlobalLima
        this.horaIngresada = horaIngresada
        this.horaSalidaReal = horaSalidaReal
        this.laboratorio = laboratorio
        this.nombreDocente = nombreDocente
        this.numeroTarjeta = numeroTarjeta
        this.cerrado = cerrado
    }

}