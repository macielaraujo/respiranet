package com.example.projetorespiranet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuBar = findViewById<BottomNavigationView>(R.id.menuBar)

        menuBar.selectedItemId = R.id.menu_home
        menuBar.setOnItemSelectedListener {
            item -> when(item.itemId){
                R.id.menu_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP) //Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.menu_maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.menu_graphics -> {
                    val intent = Intent(this, GraphicsActivity::class.java)
                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.menu_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}