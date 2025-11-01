package com.example.projetorespiranet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuBar = findViewById<BottomNavigationView>(R.id.menuBar)
        menuBar.selectedItemId = R.id.menu_settings
        menuBar.setOnItemSelectedListener {
                item -> when(item.itemId){
            R.id.menu_home -> {
                if(this !is MainActivity){
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                true
            }
            R.id.menu_maps -> {
                if (this !is MapsActivity){
                    startActivity(Intent(this, MapsActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                true
            }
            R.id.menu_graphics -> {
                if(this !is GraphicsActivity){
                    startActivity(Intent(this, GraphicsActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                true
            }
            R.id.menu_settings -> {
                if(this !is SettingsActivity){
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                true
            }
            else -> false
        }
        }
    }
}