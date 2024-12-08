package com.example.controllaboratorio.Models

import java.time.LocalTime

class Asignacion {
    var Docente: Usuario? = null
    var Laboratorio: Laboratorio? = null
    var Curso: Curso? = null
    var Dia: String? = null
    var horaEntrada: String? = null
    var horaSalida: String? = null

    constructor() {
        // Dejar vacío o inicializar con valores predeterminados si es necesario
    }

    constructor(
        Docente: Usuario?,
        Laboratorio: Laboratorio?,
        Curso: Curso?,
        Dia: String?,
        horaEntrada: String?,
        horaSalida: String?
    ) {
        this.Docente = Docente
        this.Laboratorio = Laboratorio
        this.Curso = Curso
        this.Dia = Dia
        this.horaEntrada = horaEntrada
        this.horaSalida = horaSalida
    }
}