//package com.example.projetorespiranet
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.RadioButton
//import android.widget.RadioGroup
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.projetorespiranet.databinding.ActivityMainBinding
//import com.example.projetorespiranet.network.RetrofitClient
//import com.example.projetorespiranet.network.RespiraNetApi
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import kotlinx.coroutines.Dispatchers
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import java.util.Locale
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var api: RespiraNetApi
//    private var pollingJob: Job? = null
//
//    private fun abrirTela(destino: Class<*>) {
//        if (this::class.java == destino) return // evita abrir a mesma
//
//        val intent = Intent(this, destino).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        }
//        startActivity(intent)
//        overridePendingTransition(0, 0)
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Initialize API
//        api = RetrofitClient.getApi(this)
//
//        val hoje = LocalDate.now()
//        val dia = hoje.format(DateTimeFormatter.ofPattern("dd", Locale("pt", "BR")))
//        val mes = hoje.format(DateTimeFormatter.ofPattern("MMMM", Locale("pt", "BR")))
//        val mesCapitalizado = mes.replaceFirstChar { it.uppercase() }
//        val dataFormatada = "$dia de $mesCapitalizado"
//        binding.txtDate.text = dataFormatada
//
//    // Start polling updates
//    iniciarAtualizacaoAutomatica()
//
//        //dados recebidos
////        val mensagem = "temp: 28 umidade: 52"
////        binding.txtDadosRecebidos.text = mensagem
//
//        //personalização do tema
//
//        val sharedPrefs = getSharedPreferences("respira_prefs", MODE_PRIVATE)
//        val editor = sharedPrefs.edit()
//
//        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
//        if (isDarkMode) {
//            binding.radioButtonEscuro.isChecked = true
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            editor.putBoolean("dark_mode", true)
//        } else {
//            binding.radioButtonClaro.isChecked = true
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            editor.putBoolean("dark_mode", false)
//        }
//
//        binding.radioGroupTema.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.radioButton_claro -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                    editor.putBoolean("dark_mode", false)
//                }
//                R.id.radioButton_escuro -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                    editor.putBoolean("dark_mode", true)
//                }
//            }
//            editor.apply() // Salva a escolha para a próxima vez que o app abrir
//        }
//
//
//
//        binding.menuBar.setOnItemSelectedListener {
//            item -> when (item.itemId) {
//            R.id.menu_home -> {
//                abrirTela(MainActivity::class.java)
//                true
//            }
//            R.id.menu_maps -> {
//                abrirTela(MapsActivity::class.java)
//                true
//            }
//            R.id.menu_graphics -> {
//                abrirTela(GraphicsActivity::class.java)
//                true
//            }
//            else -> false
//        }
//        }
//    }
//    override fun onResume() {
//        super.onResume()
//        binding.menuBar.selectedItemId = R.id.menu_home
//    }
//
//    private fun iniciarAtualizacaoAutomatica() {
//        pollingJob = lifecycleScope.launch {
//            // simplistic choice: nodeId 1; could be user-selected later
//            val nodeIdSelecionado = 1
//            while (isActive) {
//                atualizarDashboard(nodeIdSelecionado)
//                atualizarResumo()
//                delay(30000)
//            }
//        }
//    }
//
//    private suspend fun atualizarDashboard(nodeId: Int) {
//        try {
//            // refresh api in case base URL changed elsewhere
//            api = RetrofitClient.getApi(this@MainActivity)
//            val status = api.getNodeStatus(nodeId)
//
//            withContext(Dispatchers.Main) {
//                binding.txtValueTemp.text = "${status.sensors.temperature?.value?.toInt() ?: 0}"
//                binding.txtStatusSensorTemp.text = if (status.status == "online") "Ativo" else "Offline"
//
//                binding.txtValueUmidade.text = "${status.sensors.humidity?.value?.toInt() ?: 0}"
//                binding.txtStatusSensorUmidade.text = if (status.status == "online") "Ativo" else "Offline"
//
//                binding.cardStatusLocal.text = status.description
//                binding.cardStatusResultado.text = when (status.airQuality) {
//                    "safe" -> "Seguro"
//                    "warning" -> "Atenção"
//                    "critical" -> "Crítico"
//                    else -> "Desconhecido"
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("MainActivity", "Erro ao buscar status: ${e.message}", e)
//            withContext(Dispatchers.Main) {
//                Toast.makeText(this@MainActivity, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private suspend fun atualizarResumo() {
//        try {
//            val summary = api.getStatusSummary()
//            withContext(Dispatchers.Main) {
//                binding.contSensoresAtivos.text = "${summary.onlineNodes} Sensores"
//                binding.contSensoresOff.text = "${summary.offlineNodes} Sensores"
//                val instaveis = summary.totalNodes - summary.onlineNodes - summary.offlineNodes
//                binding.contSensoresInstaveis.text = "$instaveis Sensores"
//            }
//        } catch (e: Exception) {
//            Log.e("MainActivity", "Erro ao buscar resumo: ${e.message}", e)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        pollingJob?.cancel()
//    }
//
//}

package com.example.projetorespiranet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.projetorespiranet.databinding.ActivityMainBinding
import com.example.projetorespiranet.network.RetrofitClient
import com.example.projetorespiranet.network.RespiraNetApi
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var api: RespiraNetApi
    private var pollingJob: Job? = null

    private fun abrirTela(destino: Class<*>) {
        if (this::class.java == destino) return
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

        // Configuração de Padding para System Bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa API
        api = RetrofitClient.getApi(this)

        configurarDataAtual()
        configurarTema()
        configurarMenuNavegacao()

        // Inicia a atualização automática (Polling)
        iniciarAtualizacaoAutomatica()
    }

    private fun configurarDataAtual() {
        val hoje = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM", Locale("pt", "BR"))
        val dataFormatada = hoje.format(formatter).replaceFirstChar { it.uppercase() }
        binding.txtDate.text = dataFormatada
    }

    private fun configurarTema() {
        val sharedPrefs = getSharedPreferences("respira_prefs", MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)

        // Sincroniza o RadioGroup com o estado salvo
        if (isDarkMode) {
            binding.radioButtonEscuro.isChecked = true
        } else {
            binding.radioButtonClaro.isChecked = true
        }

        binding.radioGroupTema.setOnCheckedChangeListener { _, checkedId ->
            val editor = sharedPrefs.edit()
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
            editor.apply()
        }
    }

    private fun configurarMenuNavegacao() {
        binding.menuBar.selectedItemId = R.id.menu_home
        binding.menuBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> true
                R.id.menu_maps -> { abrirTela(MapsActivity::class.java); true }
                R.id.menu_graphics -> { abrirTela(GraphicsActivity::class.java); true }
                else -> false
            }
        }
    }

    private fun iniciarAtualizacaoAutomatica() {
        // Cancela job anterior se existir para evitar múltiplos loops
        pollingJob?.cancel()

        pollingJob = lifecycleScope.launch {
            val nodeIdInicial = 1 // RU como padrão
            while (isActive) {
                try {
                    // Executa as duas chamadas em paralelo para ganhar performance
                    coroutineScope {
                        launch { atualizarDashboard(nodeIdInicial) }
                        launch { atualizarResumo() }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Erro no Polling: ${e.message}")
                }
                delay(30000) // 30 segundos
            }
        }
    }

    private suspend fun atualizarDashboard(nodeId: Int) {
        try {
            // Re-instancia a api caso a URL tenha mudado nas configs
            val apiService = RetrofitClient.getApi(this@MainActivity)
            val status = apiService.getNodeStatus(nodeId)

            withContext(Dispatchers.Main) {
                // Formatação segura usando os novos Models
                binding.txtValueTemp.text = status.sensors.temperature?.value?.toInt()?.toString() ?: "--"
                binding.txtValueUmidade.text = status.sensors.humidity?.value?.toInt()?.toString() ?: "--"

                binding.txtStatusSensorTemp.text = if (status.status == "online") "Ativo" else "Offline"
                binding.txtStatusSensorUmidade.text = if (status.status == "online") "Ativo" else "Offline"

                binding.cardStatusLocal.text = status.description

                // Mapeamento de cor/texto para qualidade do ar
                binding.cardStatusResultado.text = when (status.airQuality.lowercase()) {
                    "safe", "bom" -> "Seguro"
                    "warning", "alerta" -> "Atenção"
                    "critical", "perigoso" -> "Crítico"
                    else -> status.airQuality
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro Dashboard: ${e.message}")
        }
    }

    private suspend fun atualizarResumo() {
        try {
            val summary = RetrofitClient.getApi(this@MainActivity).getStatusSummary()
            withContext(Dispatchers.Main) {
                binding.contSensoresAtivos.text = "${summary.onlineNodes} Sensores"
                binding.contSensoresOff.text = "${summary.offlineNodes} Sensores"

                // Cálculo de instáveis baseado nos dados da API
                val total = summary.totalNodes
                val instaveis = total - (summary.onlineNodes + summary.offlineNodes)
                binding.contSensoresInstaveis.text = "${if(instaveis > 0) instaveis else 0} Sensores"
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro Resumo: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        binding.menuBar.selectedItemId = R.id.menu_home
    }

    override fun onDestroy() {
        super.onDestroy()
        pollingJob?.cancel()
    }
}