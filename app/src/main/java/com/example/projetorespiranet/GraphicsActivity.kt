//package com.example.projetorespiranet
//
//import androidx.lifecycle.lifecycleScope
//import kotlinx.coroutines.launch
//import com.example.projetorespiranet.network.RetrofitClient
//import java.util.*
//import android.content.Intent
//import android.graphics.Color
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.widget.ArrayAdapter
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.projetorespiranet.databinding.GraphicsActivityBinding
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.DataSet
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class GraphicsActivity : AppCompatActivity() {
//
//    private lateinit var binding: GraphicsActivityBinding
//    // Dataset principal
//    private lateinit var lineDataSet1: LineDataSet
//    private lateinit var lineDataSet2: LineDataSet
//    // Objeto do gráfico
//    private lateinit var lineData1: LineData
//    private lateinit var lineData2: LineData
//
//    private var dispositivoSelecionado: String = ""
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
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = GraphicsActivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbarGraphics)
//        supportActionBar?.title = "Visualização Gráfica"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val navIcon = binding.toolbarGraphics.navigationIcon
//        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
//        binding.toolbarGraphics.navigationIcon = navIcon
//        binding.toolbarGraphics.setNavigationOnClickListener {
//            finish()
//            overridePendingTransition(0,0)
//        }
//
////        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
////            override fun run() {
////                val novoValor = (5..40).random().toFloat()
////                addEntry(novoValor)
////                Handler(Looper.getMainLooper()).postDelayed(this, 2000)
////            }
////        }, 2000)
//
//        setupChart()
//        configurarSeletor()
//        iniciarSimulacaoDeDados()
//        aplicarTemaAoGrafico(binding.chartTemp)
//        aplicarTemaAoGrafico(binding.chartHumidity)
//
//
//
//
//        binding.menuBar.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.menu_home -> {
//                    abrirTela(MainActivity::class.java)
//                    true
//                }
//                R.id.menu_maps -> {
//                    abrirTela(MapsActivity::class.java)
//                    true
//                }
//                R.id.menu_graphics -> {
//                    abrirTela(GraphicsActivity::class.java)
//                    true
//                }
//                else -> false
//            }
//        }
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.menuBar.selectedItemId = R.id.menu_graphics
//    }
//
//    private fun setupChart() {
//        val entries1 = ArrayList<Entry>()
//        val entries2 = ArrayList<Entry>()
//
//        // Configuração Dataset 1 (Temperatura - Laranja)
//        lineDataSet1 = LineDataSet(entries1, "Temperatura (°C)")
//        lineDataSet1.apply {
//            color = Color.parseColor("#FF9800") // Laranja vibrante
//            setCircleColor(Color.parseColor("#FF9800"))
//            lineWidth = 2.5f
//            circleRadius = 4f
//            setDrawCircleHole(true)
//            circleHoleColor = Color.TRANSPARENT // Efeito vazado
//            mode = LineDataSet.Mode.CUBIC_BEZIER // Linha suave/curvada
//            setDrawValues(false)
//        }
//
//        // Configuração Dataset 2 (Umidade - Ciano)
//        lineDataSet2 = LineDataSet(entries2, "Umidade (%)")
//        lineDataSet2.apply {
//            color = Color.parseColor("#00BCD4") // Ciano vibrante
//            setCircleColor(Color.parseColor("#00BCD4"))
//            lineWidth = 2.5f
//            circleRadius = 4f
//            setDrawCircleHole(true)
//            circleHoleColor = Color.TRANSPARENT
//            mode = LineDataSet.Mode.CUBIC_BEZIER
//            setDrawValues(false)
//        }
//
//        lineData1 = LineData(lineDataSet1)
//        lineData2 = LineData(lineDataSet2)
//
//        binding.chartTemp.apply {
//            data = lineData1
//            description.isEnabled = false
//            setTouchEnabled(true)
//            setPinchZoom(true)
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.setDrawGridLines(true)
//            animateX(1000) // Animação ao abrir
//        }
//
//        binding.chartHumidity.apply {
//            data = lineData2
//            description.isEnabled = false
//            setTouchEnabled(true)
//            setPinchZoom(true)
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.setDrawGridLines(true)
//            animateX(1000)
//        }
//    }
//
//    // A função de aplicar tema permanece igual, mas agora é chamada no onCreate
//    fun aplicarTemaAoGrafico(chart: LineChart) {
//        val isDarkMode = (resources.configuration.uiMode and
//                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
//                android.content.res.Configuration.UI_MODE_NIGHT_YES
//
//        // No modo escuro usamos branco, no claro usamos um cinza escuro para melhor legibilidade
//        val corTexto = if (isDarkMode) Color.WHITE else Color.DKGRAY
//        // Grade sutil
//        val corGrade = if (isDarkMode) Color.parseColor("#22FFFFFF") else Color.parseColor("#11000000")
//
//        chart.apply {
//            xAxis.textColor = corTexto
//            xAxis.gridColor = corGrade
//
//            axisLeft.textColor = corTexto
//            axisLeft.gridColor = corGrade
//
//            axisRight.isEnabled = false // Desabilita o eixo da direita para visual mais limpo
//
//            legend.textColor = corTexto
//            invalidate()
//        }
//    }
//    fun addEntry(value: Float) {
//        val index1 = lineDataSet1.entryCount // posição X automaticamente
//        val index2 = lineDataSet2.entryCount // posição X automaticamente
//
//        val newEntry1 = Entry(index1.toFloat(), value)
//        lineDataSet1.addEntry(newEntry1)
//
//        val newEntry2 = Entry(index2.toFloat(), value)
//        lineDataSet2.addEntry(newEntry2)
//
//        lineData1.notifyDataChanged()     // Notifica o dataset
//        binding.chartTemp.notifyDataSetChanged() // Notifica o gráfico
//        binding.chartTemp.invalidate()   // Redesenha
//
//        lineData2.notifyDataChanged()     // Notifica o dataset
//        binding.chartHumidity.notifyDataSetChanged() // Notifica o gráfico
//        binding.chartHumidity.invalidate()   // Redesenha
//    }
//    fun resetChart() {
//        lineDataSet1.clear()
//        lineDataSet2.clear()
//
//        lineData1.notifyDataChanged()
//        lineData2.notifyDataChanged()
//
//        binding.chartTemp.notifyDataSetChanged()
//        binding.chartHumidity.notifyDataSetChanged()
//
//        binding.chartTemp.invalidate()
//        binding.chartHumidity.invalidate()
//    }
//
//    private fun configurarSeletor() {
//        val itens = listOf("Restaurante Universitário", "Bloco 1: Merendeiro", "Bloco 2: Odontologia")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itens)
//
//        binding.autoCompleteTextView.setAdapter(adapter)
//
//        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
//            dispositivoSelecionado = itens[position]
//
//            // Exemplo: Se selecionou o primeiro item, busca o Node ID 1
//            val idParaBuscar = position + 1
//
//            resetChart()
//            atualizarGraficosComApi(idParaBuscar)
//        }
//
//        // Evento de seleção (O "Select" do Android)
//        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
//            dispositivoSelecionado = itens[position]
//
//            // 1. Limpa o gráfico atual para renderizar o novo
//            resetChart()
//
//            // 2. Simula o carregamento de dados históricos específicos (opcional)
//            gerarDadosIniciaisParaDispositivo(dispositivoSelecionado)
//
//            Toast.makeText(this, "Monitorando: $dispositivoSelecionado", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun gerarDadosIniciaisParaDispositivo(nome: String) {
//        // Aqui você poderia carregar dados reais.
//        // Vamos apenas gerar 5 pontos aleatórios para dar a sensação de mudança.
//        for (i in 1..5) {
//            val valorFake = when (nome) {
//                "Restaurante Universitário" -> (30..40).random().toFloat()
//                "Bloco 1: Merendeiro" -> (20..25).random().toFloat()
//                else -> (15..20).random().toFloat()
//            }
//            addEntry(valorFake)
//        }
//    }
//
//    private fun iniciarSimulacaoDeDados() {
//        val handler = Handler(Looper.getMainLooper())
//        handler.post(object : Runnable {
//            override fun run() {
//                // Só adiciona dados se um dispositivo estiver selecionado
//                if (dispositivoSelecionado.isNotEmpty()) {
//                    val novoValor = (10..45).random().toFloat()
//                    addEntry(novoValor)
//                }
//                handler.postDelayed(this, 2000)
//            }
//        })
//    }
//
//    private fun atualizarGraficosComApi(nodeId: Int) {
//        // O lifecycleScope garante que a chamada pare se você fechar a tela
//        lifecycleScope.launch {
//            try {
//                // 1. Busca os dados reais (Temperatura)
//                // Usamos nodeId 1 como exemplo, ou o ID vindo do seletor
//                val api = RetrofitClient.getApi(this@GraphicsActivity)
//                val listaLeituras = api.getTemperatureReadings(nodeId = nodeId, limit = 20)
//
//                // 2. Limpa os dados antigos
//                resetChart()
//
//                // 3. Adiciona cada ponto vindo da API no gráfico
//                listaLeituras.reversed().forEach { leitura ->
//                    // Usamos reversed() porque a API costuma mandar do mais novo para o mais antigo
//                    addEntry(leitura.value.toFloat())
//                }
//
//                Toast.makeText(this@GraphicsActivity, "Dados atualizados via API", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(this@GraphicsActivity, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//}


package com.example.projetorespiranet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projetorespiranet.databinding.GraphicsActivityBinding
import com.example.projetorespiranet.network.RetrofitClient
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: GraphicsActivityBinding
    private lateinit var lineDataSet1: LineDataSet
    private lateinit var lineDataSet2: LineDataSet
    private lateinit var lineData1: LineData
    private lateinit var lineData2: LineData

    private var dispositivoSelecionado: String = ""

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
        binding = GraphicsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarGraphics)
        supportActionBar?.title = "Visualização Gráfica"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarGraphics.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        setupChart()
        configurarSeletor()

        // Aplicar tema visual aos gráficos
        aplicarTemaAoGrafico(binding.chartTemp)
        aplicarTemaAoGrafico(binding.chartHumidity)

        binding.menuBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> { abrirTela(MainActivity::class.java); true }
                R.id.menu_maps -> { abrirTela(MapsActivity::class.java); true }
                R.id.menu_graphics -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.menuBar.selectedItemId = R.id.menu_graphics
    }

    private fun setupChart() {
        // Dataset 1: Temperatura
        lineDataSet1 = LineDataSet(ArrayList(), "Temperatura (°C)").apply {
            color = Color.parseColor("#FF9800")
            setCircleColor(Color.parseColor("#FF9800"))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        // Dataset 2: Umidade
        lineDataSet2 = LineDataSet(ArrayList(), "Umidade (%)").apply {
            color = Color.parseColor("#00BCD4")
            setCircleColor(Color.parseColor("#00BCD4"))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        lineData1 = LineData(lineDataSet1)
        lineData2 = LineData(lineDataSet2)

        binding.chartTemp.apply {
            data = lineData1
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            animateX(1000)
        }

        binding.chartHumidity.apply {
            data = lineData2
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            animateX(1000)
        }
    }

    private fun configurarSeletor() {
        // Itens que aparecem no Dropdown
        val itens = listOf("Restaurante Universitário", "Bloco 1: Merendeiro", "Bloco 2: Odontologia")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itens)
        binding.autoCompleteTextView.setAdapter(adapter)

        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            dispositivoSelecionado = itens[position]

            // Mapeamento: RU=ID 1, Bloco 1=ID 2, Bloco 2=ID 3 (Ajuste conforme seu banco de dados)
            val idParaBuscar = position + 1

            resetChart()
            atualizarDadosPelaApi(idParaBuscar)

            Toast.makeText(this, "Conectando ao nó: $dispositivoSelecionado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarDadosPelaApi(nodeId: Int) {
        // Chamada assíncrona para não travar a UI
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getApi(this@GraphicsActivity)

                // 1. Buscar Temperatura
                val tempReadings = api.getTemperatureReadings(nodeId = nodeId, limit = 20)
                // 2. Buscar Umidade
                val humidReadings = api.getHumidityReadings(nodeId = nodeId, limit = 20)

                resetChart()

                // Preencher gráfico de Temperatura
                tempReadings.reversed().forEachIndexed { index, reading ->
                    lineDataSet1.addEntry(Entry(index.toFloat(), reading.value.toFloat()))
                }

                // Preencher gráfico de Umidade
                humidReadings.reversed().forEachIndexed { index, reading ->
                    lineDataSet2.addEntry(Entry(index.toFloat(), reading.value.toFloat()))
                }

                // Notificar mudanças nos dois gráficos
                lineData1.notifyDataChanged()
                binding.chartTemp.notifyDataSetChanged()
                binding.chartTemp.invalidate()

                lineData2.notifyDataChanged()
                binding.chartHumidity.notifyDataSetChanged()
                binding.chartHumidity.invalidate()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@GraphicsActivity, "Erro na API: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun resetChart() {
        lineDataSet1.clear()
        lineDataSet2.clear()
        lineData1.notifyDataChanged()
        lineData2.notifyDataChanged()
        binding.chartTemp.notifyDataSetChanged()
        binding.chartHumidity.notifyDataSetChanged()
        binding.chartTemp.invalidate()
        binding.chartHumidity.invalidate()
    }

    private fun aplicarTemaAoGrafico(chart: LineChart) {
        val isDarkMode = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        val corTexto = if (isDarkMode) Color.WHITE else Color.DKGRAY
        val corGrade = if (isDarkMode) Color.parseColor("#22FFFFFF") else Color.parseColor("#11000000")

        chart.apply {
            xAxis.textColor = corTexto
            xAxis.gridColor = corGrade
            axisLeft.textColor = corTexto
            axisLeft.gridColor = corGrade
            axisRight.isEnabled = false
            legend.textColor = corTexto
            invalidate()
        }
    }
}