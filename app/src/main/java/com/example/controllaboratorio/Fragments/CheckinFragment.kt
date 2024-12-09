package com.example.controllaboratorio.Fragments

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.controllaboratorio.Models.Asignacion
import com.example.controllaboratorio.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CheckinFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var entradaHoraDocente: String
    private lateinit var laboratorioIngresado: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkin, container, false)
        val timeEditText: TextInputEditText = view.findViewById(R.id.time_edit_text_tarjeta)
        val laboratorioSpinner: Spinner = view.findViewById(R.id.spinner8)
        val tarjetaButton: MaterialButton = view.findViewById(R.id.TarjetaButton)
        val cerrarSalonButton: MaterialButton = view.findViewById(R.id.CerrarSalonButton)


        // Verificar si ya hay un acceso abierto
        verificarAccesoAbierto()

        cargarLaboratorios { laboratorios ->
            // Configurar el Spinner con los nombres de los laboratorios
            configurarSpinnerLaboratorios(laboratorioSpinner, laboratorios)
        }

        timeEditText.setOnClickListener {
            showTimePicker(timeEditText)
        }

        tarjetaButton.setOnClickListener {
            verificarYRegistrarAcceso()
            recargarFragment()
        }

        cerrarSalonButton.setOnClickListener {
            cerrarSalon()
            recargarFragment()
        }

        return view
    }


    private fun cargarLaboratorios(callback: (List<String>) -> Unit) {
        firestore.collection("ambientes").get()
            .addOnSuccessListener { result ->
                val laboratorios = result.documents.mapNotNull { it.getString("nombreLab") }
                callback(laboratorios)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar laboratorios: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(emptyList())
            }
    }

    private fun configurarSpinnerLaboratorios(spinner: Spinner, laboratorios: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, laboratorios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                laboratorioIngresado = laboratorios[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                laboratorioIngresado = "" // Si no se selecciona nada, dejamos la variable vacía
            }
        }
    }

    private fun showTimePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                entradaHoraDocente = formattedTime
                editText.setText(formattedTime)
            },
            hour,
            minute,
            true
        )

        timePicker.show()
    }

    private fun verificarYRegistrarAcceso() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getString("userId", null)

        if (usuarioId.isNullOrEmpty()) {
            Toast.makeText(context, "No se encontró el usuario logeado", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("usuarios").document(usuarioId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombreDocente = document.getString("nombre")
                    val numeroTarjeta = document.getLong("NumTarjeta")

                    if (nombreDocente != null && numeroTarjeta != null) {
                        verificarDuplicadoYRegistrar(nombreDocente, numeroTarjeta)
                    } else {
                        Toast.makeText(context, "Información del docente incompleta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "El docente no está registrado", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verificarDuplicadoYRegistrar(nombreDocente: String, numeroTarjeta: Long) {
        val fechaActual = getCurrentDate()

        // Obtener las asignaciones del docente para el día actual
        firestore.collection("asignaciones")
            .whereEqualTo("docente.nombre", nombreDocente)
            .get()
            .addOnSuccessListener { result ->
                val asignaciones = result.mapNotNull { it.toObject(Asignacion::class.java) }
                // Filtrar las asignaciones para ese día
                val asignacionesHoy = asignaciones.filter { it.Dia == getCurrentDayOfWeek() }

                if (asignacionesHoy.isNotEmpty()) {
                    // Verificar si el laboratorio del spinner coincide con alguno de los asignados para ese día
                    val laboratorioAsignado = asignacionesHoy.find { it.Laboratorio?.nombreLab == laboratorioIngresado }

                    if (laboratorioAsignado != null) {
                        // Verificar duplicados en la colección "accesos" con la combinación de fecha, docente y laboratorio
                        firestore.collection("accesos")
                            .whereEqualTo("nombreDocente", nombreDocente)
                            .whereEqualTo("fecha", fechaActual)
                            .whereEqualTo("laboratorio.nombreLab", laboratorioIngresado) // Verificar laboratorio también
                            .get()
                            .addOnSuccessListener { result ->
                                if (result.isEmpty) {
                                    // Si no existe un registro previo con la misma fecha y laboratorio, proceder con el registro
                                    obtenerAsignacionesDocente(nombreDocente, numeroTarjeta)
                                } else {
                                    Toast.makeText(context, "Ya se ha registrado el check-in para este laboratorio hoy", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error al verificar duplicados: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "El laboratorio seleccionado no coincide con el asignado para hoy", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No se encontró la asignación del docente para hoy", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al obtener asignaciones: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun obtenerAsignacionesDocente(nombreDocente: String?, numeroTarjeta: Long?) {
        firestore.collection("asignaciones")
            .whereEqualTo("docente.nombre", nombreDocente)
            .get()
            .addOnSuccessListener { result ->
                // Suponemos que obtenemos la primera asignación de las posibles
                val asignacion = result.firstOrNull()?.toObject(Asignacion::class.java)

                if (asignacion != null) {
                    // Establecer el laboratorio seleccionado por defecto en el Spinner
                    laboratorioIngresado = asignacion.Laboratorio?.nombreLab ?: ""
                    val spinner = view?.findViewById<Spinner>(R.id.spinner8)
                    val adapter = spinner?.adapter as ArrayAdapter<String>

                    // Verificar si el laboratorio está en la lista y seleccionarlo
                    val index = adapter.getPosition(laboratorioIngresado)
                    if (index >= 0) {
                        spinner.setSelection(index) // Establecer la selección del laboratorio
                    }

                    // Resto de las validaciones de la asignación y el horario
                    val diaActual = getCurrentDayOfWeek()
                    if (diaActual != asignacion.Dia) {
                        Toast.makeText(context, "Hoy no tienes asignación en este laboratorio", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Comprobamos si el laboratorio y la tarjeta son correctos
                    if (asignacion.Laboratorio?.nombreLab == laboratorioIngresado && asignacion.Docente?.NumTarjeta == numeroTarjeta) {
                        // Verificar las horas
                        val horaGlobalLima = obtenerHoraLima()

                        if (esHoraValida(horaGlobalLima, asignacion.horaEntrada, asignacion.horaSalida)) {
                            // Si ambas horas (la global y la ingresada) están dentro del rango
                            if (esHoraValida(entradaHoraDocente, asignacion.horaEntrada, asignacion.horaSalida)) {
                                registrarAcceso(asignacion, horaGlobalLima)
                            } else {
                                Toast.makeText(context, "La hora ingresada no está dentro del rango asignado", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "La hora global de Lima no está dentro del rango asignado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "El laboratorio o número de tarjeta no coinciden", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No se encontró la asignación del docente", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun obtenerHoraLima(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
        val hora = calendar.get(Calendar.HOUR_OF_DAY)
        val minuto = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hora, minuto)
    }

    private fun esHoraValida(hora: String?, horaEntrada: String?, horaSalida: String?): Boolean {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaIngresada = format.parse(hora)
        val horaIni = format.parse(horaEntrada)
        val horaFin = format.parse(horaSalida)

        return horaIngresada != null && horaIngresada.after(horaIni) && horaIngresada.before(horaFin)
    }

    private fun registrarAcceso(asignacion: Asignacion, horaGlobalLima: String) {
        val acceso = mapOf(
            "fecha" to getCurrentDate(),
            "cerrado" to false,
            "nombreDocente" to asignacion.Docente?.Nombre,
            "numeroTarjeta" to asignacion.Docente?.NumTarjeta,
            "horaIngresada" to entradaHoraDocente,
            "horaGlobalLima" to horaGlobalLima,
            "laboratorio" to asignacion.Laboratorio,
            "curso" to asignacion.Curso?.nombreCurso,
            "grupo" to asignacion.Curso?.grupo,
            "horaEntrada" to asignacion.horaEntrada,
            "horaSalida" to asignacion.horaSalida
        )

        firestore.collection("accesos").add(acceso)
            .addOnSuccessListener {
                Toast.makeText(context, "Check-in registrado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al registrar el acceso: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miercoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sabado"
            else -> ""
        }
    }

    private fun cerrarSalon() {
        val fechaActual = getCurrentDate()
        val cerrarSalonButton: MaterialButton = view?.findViewById(R.id.CerrarSalonButton) ?: return

        // Deshabilitar el botón para que no se pueda presionar nuevamente si ya está cerrado
        cerrarSalonButton.isEnabled = false

        // Lógica para cerrar el salón
        firestore.collection("accesos")
            .whereEqualTo("fecha", fechaActual)
            .whereEqualTo("laboratorio.nombreLab", laboratorioIngresado)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(context, "No hay registros de acceso para este laboratorio hoy", Toast.LENGTH_SHORT).show()
                    // Si no hay registros de acceso, habilitar el botón de nuevo
                    cerrarSalonButton.isEnabled = true
                } else {
                    // Si hay registros de acceso, cerrar el salón
                    result.forEach { document ->
                        val accesoId = document.id
                        firestore.collection("accesos").document(accesoId)
                            .update("cerrado", true)
                            .addOnSuccessListener {
                                Toast.makeText(context, "El salón se ha cerrado exitosamente", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error al cerrar el salón: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    // No permitir cerrar el salón nuevamente
                    cerrarSalonButton.isEnabled = false
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al verificar el cierre: ${e.message}", Toast.LENGTH_SHORT).show()
                // Si ocurre un error, habilitar el botón para que el usuario pueda intentarlo de nuevo
                cerrarSalonButton.isEnabled = true
            }
    }



    private fun verificarAccesoAbierto() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getString("userId", null)

        if (usuarioId.isNullOrEmpty()) {
            Toast.makeText(context, "No se encontró el usuario logeado", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("usuarios").document(usuarioId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombreDocente = document.getString("nombre")

                    if (nombreDocente != null) {
                        val fechaActual = getCurrentDate()
                        // Buscar un acceso abierto para este docente
                        firestore.collection("accesos")
                            .whereEqualTo("nombreDocente", nombreDocente)
                            .whereEqualTo("fecha", fechaActual)
                            .whereEqualTo("cerrado", false) // Verificar que el acceso no esté cerrado
                            .get()
                            .addOnSuccessListener { result ->
                                if (result.isEmpty) {
                                    // Si no hay acceso abierto, el docente puede registrar su check-in
                                    habilitarCheckin(true)
                                } else {
                                    // Si hay un acceso abierto, deshabilitar el spinner y habilitar el botón de cierre
                                    val accesoAbierto = result.firstOrNull()
                                    val laboratorioAbierto = accesoAbierto?.getString("laboratorio.nombreLab") ?: ""
                                    laboratorioIngresado = laboratorioAbierto

                                    // Establecer el laboratorio en el spinner
                                    val spinner = view?.findViewById<Spinner>(R.id.spinner8)
                                    val adapter = spinner?.adapter as ArrayAdapter<String>
                                    val index = adapter.getPosition(laboratorioAbierto)
                                    if (index >= 0) {
                                        spinner.setSelection(index) // Establecer la selección en el laboratorio abierto
                                    }

                                    habilitarCheckin(false)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error al verificar acceso: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
    }

    private fun habilitarCheckin(hacer: Boolean) {
        // Habilitar el spinner para que el docente pueda seleccionar un laboratorio
        val spinner = view?.findViewById<Spinner>(R.id.spinner8)
        spinner?.isEnabled = hacer

        // Habilitar el botón de check-in
        val tarjetaButton: MaterialButton = view?.findViewById(R.id.TarjetaButton) ?: return
        tarjetaButton.isEnabled = hacer
        if(tarjetaButton.isEnabled){
            tarjetaButton.visibility = View.VISIBLE
        } else {
            tarjetaButton.visibility = View.GONE
        }

        // Deshabilitar el botón de "Cerrar Salón", ya que no es posible cerrarlo si no hay un acceso abierto
        val cerrarSalonButton: MaterialButton = view?.findViewById(R.id.CerrarSalonButton) ?: return
        cerrarSalonButton.isEnabled = !hacer
        if(cerrarSalonButton.isEnabled){
            cerrarSalonButton.visibility = View.VISIBLE
        } else {
            cerrarSalonButton.visibility = View.GONE
        }

    }

    private fun recargarFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.checkin_fragment, CheckinFragment()) // Asegúrate de usar el ID correcto del contenedor
        fragmentTransaction.commit()
    }


}
