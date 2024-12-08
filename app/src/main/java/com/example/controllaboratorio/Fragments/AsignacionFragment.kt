package com.example.controllaboratorio.Fragments

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.controllaboratorio.Models.Curso
import com.example.controllaboratorio.Models.Laboratorio
import com.example.controllaboratorio.Models.Usuario
import com.example.controllaboratorio.R
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import com.google.firebase.firestore.FirebaseFirestore
import com.example.controllaboratorio.Models.Asignacion
import java.time.LocalTime
import java.time.LocalTime.parse
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

class AsignacionFragment : Fragment() {

    private var listaDocentes: List<Usuario> = listOf()
    private var listaLaboratorios: List<Laboratorio> = listOf()
    private var listaCursos: List<Curso> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_asignacion, container, false)
        loadSpinners(view)
        val timeEditTextEntrada: TextInputEditText = view.findViewById(R.id.time_edit_text)
        val timeEditTextSalida: TextInputEditText = view.findViewById(R.id.time_edit_text_salida)
        val asignarButton: Button = view.findViewById(R.id.button2)

        timeEditTextEntrada.setOnClickListener {
            showTimePicker(timeEditTextEntrada)
        }

        timeEditTextSalida.setOnClickListener {
            showTimePicker(timeEditTextSalida)
        }

        asignarButton.setOnClickListener {
            guardarAsignacion(view)
        }

        return view
    }

    private fun showTimePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(formattedTime)
            },
            hour,
            minute,
            true
        )

        timePicker.show()
    }

    private fun loadSpinners(view: View) {
        val firestore = FirebaseFirestore.getInstance()

        // Spinner para docentes
        firestore.collection("usuarios")
            .whereEqualTo("rol", "docente")
            .get()
            .addOnSuccessListener { documents ->
                listaDocentes = documents.map { it.toObject(Usuario::class.java) }
                val docenteAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listaDocentes.map { it.Nombre ?: "Sin Nombre" }
                )
                docenteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.findViewById<Spinner>(R.id.spinner6)?.adapter = docenteAdapter
            }

        // Spinner para laboratorios
        firestore.collection("ambientes")
            .get()
            .addOnSuccessListener { documents ->
                listaLaboratorios = documents.map { it.toObject(Laboratorio::class.java) }
                val labAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listaLaboratorios.map { it.nombreLab ?: "Sin Nombre" }
                )
                labAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.findViewById<Spinner>(R.id.spinner4)?.adapter = labAdapter
            }

        // Spinner para cursos
        firestore.collection("cursos")
            .get()
            .addOnSuccessListener { documents ->
                listaCursos = documents.map { it.toObject(Curso::class.java) }
                val cursoAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    listaCursos.map { "${it.nombreCurso} - ${it.grupo}" }
                )
                cursoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.findViewById<Spinner>(R.id.spinner5)?.adapter = cursoAdapter
            }
    }

    private fun guardarAsignacion(view: View) {
        val docenteSpinner = view.findViewById<Spinner>(R.id.spinner6)
        val laboratorioSpinner = view.findViewById<Spinner>(R.id.spinner4)
        val cursoSpinner = view.findViewById<Spinner>(R.id.spinner5)
        val diaSpinner = view.findViewById<Spinner>(R.id.dia)
        val entradaEditText = view.findViewById<TextInputEditText>(R.id.time_edit_text)
        val salidaEditText = view.findViewById<TextInputEditText>(R.id.time_edit_text_salida)

        val docenteSeleccionado = docenteSpinner.selectedItem?.toString()
        val laboratorioSeleccionado = laboratorioSpinner.selectedItem?.toString()
        val cursoSeleccionado = cursoSpinner.selectedItem?.toString()
        val diaSeleccionado = diaSpinner.selectedItem?.toString()
        val horaEntrada = entradaEditText.text.toString()
        val horaSalida = salidaEditText.text.toString()

        // Validación de campos vacíos
        if (docenteSeleccionado.isNullOrEmpty() || laboratorioSeleccionado.isNullOrEmpty() ||
            cursoSeleccionado.isNullOrEmpty() || diaSeleccionado.isNullOrEmpty() ||
            horaEntrada.isEmpty() || horaSalida.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val docente = listaDocentes.find { it.Nombre == docenteSeleccionado }
        val laboratorio = listaLaboratorios.find { it.nombreLab == laboratorioSeleccionado }
        val curso = listaCursos.find { "${it.nombreCurso} - ${it.grupo}" == cursoSeleccionado }

        // Validar que los datos no sean nulos
        if (docente != null && laboratorio != null && curso != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("asignaciones")
                .get()
                .addOnSuccessListener { documents ->
                    var solapamiento = false
                    var grupoDuplicado = false
                    var docenteSolapado = false

                    val nuevaHoraEntrada = LocalTime.parse(horaEntrada)
                    val nuevaHoraSalida = LocalTime.parse(horaSalida)

                    for (document in documents) {
                        val asignacion = document.toObject(Asignacion::class.java)

                        // Validar si el laboratorio está ocupado en ese horario
                        if (asignacion.Laboratorio?.nombreLab == laboratorio.nombreLab &&
                            asignacion.Dia == diaSeleccionado) {
                            val horaEntradaExistente = LocalTime.parse(asignacion.horaEntrada)
                            val horaSalidaExistente = LocalTime.parse(asignacion.horaSalida)

                            if (nuevaHoraEntrada.isBefore(horaSalidaExistente) && nuevaHoraSalida.isAfter(horaEntradaExistente)) {
                                solapamiento = true
                                break
                            }
                        }

                        // Validar si el grupo del curso ya tiene una asignación
                        if (asignacion.Curso?.nombreCurso == curso.nombreCurso &&
                            asignacion.Curso?.grupo == curso.grupo) {
                            grupoDuplicado = true
                            break
                        }

                        // Validar si el docente está asignado en el mismo rango de tiempo
                        if (asignacion.Docente?.Nombre == docente.Nombre &&
                            asignacion.Dia == diaSeleccionado) {
                            val horaEntradaExistente = LocalTime.parse(asignacion.horaEntrada)
                            val horaSalidaExistente = LocalTime.parse(asignacion.horaSalida)

                            if (nuevaHoraEntrada.isBefore(horaSalidaExistente) && nuevaHoraSalida.isAfter(horaEntradaExistente)) {
                                docenteSolapado = true
                                break
                            }
                        }
                    }

                    if (solapamiento) {
                        Toast.makeText(requireContext(), "El laboratorio ya está asignado en este horario", Toast.LENGTH_SHORT).show()
                    } else if (grupoDuplicado) {
                        Toast.makeText(requireContext(), "Este grupo ya tiene un laboratorio asignado", Toast.LENGTH_SHORT).show()
                    } else if (docenteSolapado) {
                        Toast.makeText(requireContext(), "El docente ya está asignado en este rango de horas", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si no hay conflictos, guardar la nueva asignación
                        val asignacion = Asignacion(
                            Docente = docente,
                            Laboratorio = laboratorio,
                            Curso = curso,
                            Dia = diaSeleccionado,
                            horaEntrada = horaEntrada,
                            horaSalida = horaSalida
                        )

                        firestore.collection("asignaciones")
                            .add(asignacion)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Asignación guardada con éxito", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(requireContext(), "Error al guardar: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error al verificar disponibilidad: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Error al seleccionar datos", Toast.LENGTH_SHORT).show()
        }
    }







}