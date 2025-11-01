package com.example.projetorespiranet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
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

        val toolBar = findViewById<Toolbar>(R.id.toolbar_settings)
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Configurações"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navIcon = toolBar.navigationIcon
        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
        toolBar.navigationIcon = navIcon
        toolBar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }
    }
}