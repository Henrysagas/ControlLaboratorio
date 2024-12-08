package com.example.controllaboratorio.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.controllaboratorio.Fragments.AsignacionFragment
import com.example.controllaboratorio.Fragments.CheckinFragment
import com.example.controllaboratorio.R

import com.example.controllaboratorio.Fragments.DocenteFragment
import com.example.controllaboratorio.Fragments.LabFragment
import com.example.controllaboratorio.Fragments.PerfilFragment
import com.example.controllaboratorio.Fragments.ReporteFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DocenteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docente)
        enableEdgeToEdge()

        // Reemplaza el contenedor con el fragmento
        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val inicioFragment = DocenteFragment()

            // Reemplazar el fragmento en el contenedor de FrameLayout
            fragmentTransaction.replace(R.id.fragment_container_docente, inicioFragment)
            fragmentTransaction.commit()
        }

        //Configurar el BottomNavigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_docente)
        bottomNavigation.setOnItemSelectedListener{ menuItem ->
            val fragment = when (menuItem.itemId){
                R.id.navigation_horario -> DocenteFragment()
                R.id.navigation_historial_docente -> ReporteFragment()
                R.id.navigation_check -> CheckinFragment()
                R.id.navigation_perfildocente -> PerfilFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_docente, it)
                    .commit()
            }
            true
        }
    }
}
