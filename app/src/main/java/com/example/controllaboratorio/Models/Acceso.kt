package com.example.controllaboratorio.Models

import java.time.LocalDate
import java.time.LocalTime

class Acceso {

    var asignacion: Asignacion? = null
    var fecha: LocalDate? = null
    var horaEntrada: LocalTime? = null
    var horaSalida: LocalTime? = null
    var Estado: String? = null

    constructor(
        asignacion: Asignacion?,
        fecha: LocalDate?,
        horaEntrada: LocalTime?,
        horaSalida: LocalTime?,
        Estado: String
    ) {
        this.asignacion = asignacion
        this.fecha = fecha
        this.horaEntrada = horaEntrada
        this.horaSalida = horaSalida
        this.Estado = Estado
    }
}