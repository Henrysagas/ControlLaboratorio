package com.example.controllaboratorio.Models

class Curso {
    var nombreCurso: String? = null
    var grupo: String? = null

    constructor() {
        // Dejar vacío o inicializar con valores predeterminados si es necesario
    }

    constructor(nombreCurso: String?, grupo: String?) {
        this.nombreCurso = nombreCurso
        this.grupo = grupo
    }
}