package com.example.controllaboratorio.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.controllaboratorio.Fragments.AsignacionFragment
import com.example.controllaboratorio.Fragments.PerfilFragment
import com.example.controllaboratorio.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()

        // Reemplaza el contenedor con el fragmento
        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val inicioFragment = AsignacionFragment()

            // Reemplazar el fragmento en el contenedor de FrameLayout
            fragmentTransaction.replace(R.id.fragment_container, inicioFragment)
            fragmentTransaction.commit()
        }
        //Configurar el BottomNavigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener{ menuItem ->
            val fragment = when (menuItem.itemId){
                R.id.navigation_lab -> AsignacionFragment()
                R.id.navigation_perfil -> PerfilFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }
    }
}
