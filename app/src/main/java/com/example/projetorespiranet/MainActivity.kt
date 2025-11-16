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
import com.example.projetorespiranet.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
        val mensagem = "temp: 28 umidade: 52"
        binding.txtDadosRecebidos.text = mensagem

        //personalização do tema
        val checkedId = binding.radioGroupTema.checkedRadioButtonId

        if (checkedId != -1) {
            val selected = findViewById<RadioButton>(checkedId)
            val temaSelecionado = selected.text.toString()
            Log.d("RADIO", "Selecionado agora: $temaSelecionado")
        }


        binding.menuBar.selectedItemId = R.id.menu_home
        binding.menuBar.setOnItemSelectedListener {
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