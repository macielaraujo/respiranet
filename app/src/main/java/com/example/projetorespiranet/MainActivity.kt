package com.example.projetorespiranet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.projetorespiranet.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun abrirTela(destino: Class<*>) {
        if (this::class.java == destino) return // evita abrir a mesma

        val intent = Intent(this, destino).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val hoje = LocalDate.now()
        val dia = hoje.format(DateTimeFormatter.ofPattern("dd", Locale("pt", "BR")))
        val mes = hoje.format(DateTimeFormatter.ofPattern("MMMM", Locale("pt", "BR")))
        val mesCapitalizado = mes.replaceFirstChar { it.uppercase() }
        val dataFormatada = "$dia de $mesCapitalizado"
        binding.txtDate.text = dataFormatada

//        Dados do card de Umidade
        binding.txtValueUmidade.text = "52"
        binding.txtStatusSensorUmidade.text = "Ativo"

        //Dados do card de Temperatura
        binding.txtValueTemp.text = "28"
        binding.txtStatusSensorTemp.text = "Ativo"

        //Dados do card de condição do ambiente
        binding.cardStatusLocal.text = "Merendeiro"
        binding.cardStatusResultado.text = "Seguro"

        //status dos dispositivos
        val contAtivos = 7
        val contInstaveis = 3
        val contDesativados = 2
        binding.contSensoresAtivos.text = "$contAtivos Sensores"
        binding.contSensoresInstaveis.text = "$contInstaveis Sensores"
        binding.contSensoresOff.text = "$contDesativados Sensores"

        //dados recebidos
//        val mensagem = "temp: 28 umidade: 52"
//        binding.txtDadosRecebidos.text = mensagem

        //personalização do tema

        val sharedPrefs = getSharedPreferences("respira_prefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        if (isDarkMode) {
            binding.radioButtonEscuro.isChecked = true
        } else {
            binding.radioButtonClaro.isChecked = true
        }

        binding.radioGroupTema.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton_claro -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    editor.putBoolean("dark_mode", false)
                }
                R.id.radioButton_escuro -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    editor.putBoolean("dark_mode", true)
                }
            }
            editor.apply() // Salva a escolha para a próxima vez que o app abrir
        }



        binding.menuBar.setOnItemSelectedListener {
            item -> when (item.itemId) {
            R.id.menu_home -> {
                abrirTela(MainActivity::class.java)
                true
            }
            R.id.menu_maps -> {
                abrirTela(MapsActivity::class.java)
                true
            }
            R.id.menu_graphics -> {
                abrirTela(GraphicsActivity::class.java)
                true
            }
            else -> false
        }
        }
    }
    override fun onResume() {
        super.onResume()
        binding.menuBar.selectedItemId = R.id.menu_home
    }

}