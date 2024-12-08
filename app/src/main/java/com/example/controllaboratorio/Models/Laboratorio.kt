package com.example.controllaboratorio.Models

class Laboratorio {
    var nombrelab: String? = null
    var aforo: Int? = 0

    constructor(aforo: Int?, nombrelab: String?) {
        this.aforo = aforo
        this.nombrelab = nombrelab
    }
}