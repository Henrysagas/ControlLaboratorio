package com.example.controllaboratorio.Fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.controllaboratorio.R
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AsignacionFragment : Fragment() {

    //private lateinit var timeEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asignacion, container, false)*/
        val view = inflater.inflate(R.layout.fragment_asignacion, container, false)
        val timeEditTextEntrada: TextInputEditText = view.findViewById(R.id.time_edit_text)
        val timeEditTextSalida: TextInputEditText = view.findViewById(R.id.time_edit_text_salida)

        // Configurar el click listener para abrir el TimePickerDialog
        timeEditTextEntrada.setOnClickListener {
            showTimePicker(timeEditTextEntrada)
        }

        timeEditTextSalida.setOnClickListener{
            showTimePicker(timeEditTextSalida)
        }
        return view
    }

    private fun showTimePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Crear el TimePickerDialog
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                // Formatear la hora seleccionada y actualizar el EditText
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(formattedTime)
            },
            hour,
            minute,
            true // Formato de 24 horas
        )

        // Mostrar el TimePickerDialog
        timePicker.show()
    }
}