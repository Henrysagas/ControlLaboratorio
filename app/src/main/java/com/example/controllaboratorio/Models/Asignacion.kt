package com.example.controllaboratorio.Models

import java.time.LocalTime

class Asignacion {
    var Docente: Usuario? = null
    var Laboratorio: Laboratorio? = null
    var Curso: Curso? = null
    var Dia: String? = null
    var horaEntrada: LocalTime? = null
    var horaSalida: LocalTime? = null

    constructor(
        Docente: Usuario?,
        Laboratorio: Laboratorio?,
        Curso: Curso?,
        Dia: String?,
        horaEntrada: LocalTime?,
        horaSalida: LocalTime?
    ) {
        this.Docente = Docente
        this.Laboratorio = Laboratorio
        this.Curso = Curso
        this.Dia = Dia
        this.horaEntrada = horaEntrada
        this.horaSalida = horaSalida
    }
}