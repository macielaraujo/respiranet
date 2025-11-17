package com.example.projetorespiranet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetorespiranet.databinding.MapsActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: MapsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.menuBar.selectedItemId = R.id.menu_maps
        binding.menuBar.setOnItemSelectedListener {
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

        setSupportActionBar(binding.toolbarMaps)
        supportActionBar?.title = "Ver mapa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navIcon = binding.toolbarMaps.navigationIcon
        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
        binding.toolbarMaps.navigationIcon = navIcon
        binding.toolbarMaps.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }



    }

}