package com.example.controllaboratorio.Fragments

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.controllaboratorio.R
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar


class CheckinFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_checkin, container, false)
        val timeEditText: TextInputEditText = view.findViewById(R.id.time_edit_text_tarjeta)

        // Configurar el click listener para abrir el TimePickerDialog
        timeEditText.setOnClickListener{
            showTimePicker(timeEditText)
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