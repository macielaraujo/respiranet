package com.example.projetorespiranet

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetorespiranet.databinding.GraphicsActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: GraphicsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GraphicsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.graphicsview)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        setSupportActionBar(binding.toolbarGraphics)
        supportActionBar?.title = "Visualização Gráfica"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navIcon = binding.toolbarGraphics.navigationIcon
        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
        binding.toolbarGraphics.navigationIcon = navIcon
        binding.toolbarGraphics.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        // Lista de dados
        val itens = listOf("ESP 1", "ESP 2", "ESP 3")
        // Adapter padrão do Android
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            itens
        )
        // Conecta o adapter ao ListView
        binding.listItemsDevices.adapter = adapter
        // Clique em item da lista
        binding.listItemsDevices.setOnItemClickListener { _, _, position, _ ->
            val item = itens[position]
            Toast.makeText(this, "Clicou em: $item", Toast.LENGTH_SHORT).show()
        }

        binding.menuBar.selectedItemId = R.id.menu_graphics
        binding.menuBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.menu_maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.menu_graphics -> {
                    val intent = Intent(this, GraphicsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.menu_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }

    }
}