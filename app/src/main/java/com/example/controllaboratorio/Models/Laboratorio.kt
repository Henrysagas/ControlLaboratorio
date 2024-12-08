package com.example.controllaboratorio.Models

class Laboratorio {
    var nombreLab: String? = null
    var aforo: Int? = 0

    constructor() {
        // Dejar vacío o inicializar con valores predeterminados si es necesario
    }

    constructor(aforo: Int?, nombrelab: String?) {
        this.aforo = aforo
        this.nombreLab = nombrelab
    }
}