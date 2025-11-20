//package com.example.projetorespiranet
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.projetorespiranet.databinding.SettingsActivityBinding
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class SettingsActivity : AppCompatActivity() {
//
//    private lateinit var binding: SettingsActivityBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = SettingsActivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.menuBar.selectedItemId = R.id.menu_settings
//        binding.menuBar.setOnItemSelectedListener {
//                item -> when(item.itemId){
//            R.id.menu_home -> {
//                val intent = Intent(this, MainActivity::class.java)
//                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//                overridePendingTransition(0, 0)
//                true
//            }
//            R.id.menu_maps -> {
//                val intent = Intent(this, MapsActivity::class.java)
//                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//                overridePendingTransition(0, 0)
//                true
//            }
//            R.id.menu_graphics -> {
//                val intent = Intent(this, GraphicsActivity::class.java)
//                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//                overridePendingTransition(0, 0)
//                true
//            }
//            R.id.menu_settings -> {
//                val intent = Intent(this, SettingsActivity::class.java)
//                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//                overridePendingTransition(0, 0)
//                true
//            }
//            else -> false
//        }
//        }
//
//        setSupportActionBar(binding.toolbarSettings)
//        supportActionBar?.title = "Configurações"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val navIcon = binding.toolbarSettings.navigationIcon
//        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
//        binding.toolbarSettings.navigationIcon = navIcon
//        binding.toolbarSettings.setNavigationOnClickListener {
//            finish()
//            overridePendingTransition(0,0)
//        }
//    }
//}